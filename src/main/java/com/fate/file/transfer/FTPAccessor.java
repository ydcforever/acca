package com.fate.file.transfer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

/**
 * Created by ydc on 2019/7/2.
 */
public final class FTPAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(FTPAccessor.class);

    private static final long SLEEP_STEP = 5000;

    private static final int BATCH_SIZE = 25;

    private static final int FTP_MAX_RETRY = 5;

    /**
     * 30 s, half a minute
     */
    protected static final int RELOAD_WAIT_TIME = 30000;

    protected static final int RELOAD_MAX_TRY = 4;

    /**
     * default ftpFileProcessor
     * @param host
     * @param port
     * @param user
     * @param password
     * @param fileSelector
     * @param downloadDir
     * @param binary
     * @param ftpDir
     * @throws Exception
     */
    public static void accessWithFtpFileProcessor(String host, int port, String user, String password,
                          FileSelector fileSelector, String downloadDir, boolean binary, String... ftpDir) throws Exception {
        FTPFileProcessor ftpFileProcessor = new FTPFileProcessor() {
            @Override
            public void doWith(FTPClient ftp, FTPFile[] files, String downloadDir) throws Exception {
                doWithDefault(ftp, files, fileSelector, downloadDir);
            }
        };
        access(host, port, user, password, ftpFileProcessor, downloadDir, binary, ftpDir);
    }

    public static void access(String host, int port, String user, String password,
                              FTPFileProcessor ftpFileProcessor, String downloadDir,
                              boolean binary, String... ftpDir) throws Exception {
        FTPClient ftp = new FTPClient();
        RedoWrapper rw = new RedoWrapper();
        while (rw.needRedo) {
            rw.needRedo = false;
            try {
                ftp.setControlKeepAliveTimeout(60);
                ftp.connect(host, port);
                int reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    afterConnectionRefused(rw, reply);
                } else if (ftp.login(user, password)) {
                    afterLogin(ftp, ftpFileProcessor, downloadDir, binary, ftpDir);
                } else {
                    afterLoginFailed(rw);
                }
            } catch (SocketException e) {
                afterConnectException(rw, e);
            } finally {
                closeFTP(ftp);
            }
        }
    }

    private static void afterConnectionRefused(RedoWrapper rw, int reply) throws InterruptedException {
        LOG.warn("FTP server refused connection, reply code: {}", reply);
        sleepBeforeRedo(rw);
    }

    private static void afterLoginFailed(RedoWrapper rw) throws InterruptedException {
        LOG.warn("Failed to login to FTP server");
        sleepBeforeRedo(rw);
    }

    private static void afterConnectException(RedoWrapper rw, SocketException e) throws InterruptedException {
        LOG.warn(e.getMessage(), e);
        sleepBeforeRedo(rw);
    }

    private static void sleepBeforeRedo(RedoWrapper rw) throws InterruptedException {
        int times = ++rw.redoCount;
        if (times <= FTP_MAX_RETRY) {
            rw.needRedo = true;
            Thread.sleep(SLEEP_STEP * times);
            LOG.warn("Began to do {} time(s) retry...", times);
        }
    }

    private static void afterLogin(FTPClient ftp, FTPFileProcessor ftpFileProcessor, String downloadDir, boolean binary, String... ftpDir) throws Exception {
        ftp.enterLocalPassiveMode();
        if (binary) {
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        }
        FTPListParseEngine engine;
        for (String dir : ftpDir) {
            if (ftp.changeWorkingDirectory(dir)) {
                engine = ftp.initiateListParsing();
                while (engine.hasNext()) {
                    FTPFile[] files = engine.getNext(BATCH_SIZE);
                    ftpFileProcessor.doWith(ftp, files, downloadDir);
                }
            }
        }
        ftp.logout();
    }

    public interface FTPFileProcessor {
        /**
         *
         * @param ftp
         * @param files
         * @param downloadDir
         * @throws Exception
         */
        void doWith(FTPClient ftp, FTPFile[] files, String downloadDir) throws Exception;
    }

    /**
     * apply to some ftp files
     * @param ftp
     * @param files
     * @param fileSelector
     * @param downloadDir
     * @throws Exception
     */
    public static boolean doWithDefault(FTPClient ftp, FTPFile[] files, FileSelector fileSelector, String downloadDir) throws Exception {
        boolean find = false;
        for (FTPFile file : files) {
            if (file.isFile()) {
                String remoteFileName = file.getName();
                if (fileSelector.acceptFile(remoteFileName)) {
                    String order = FileSelector.getOrder(remoteFileName, fileSelector.getRegexp());
                    if (fileSelector.acceptOrder(order)) {
                        find = true;
                        long remoteFileLength = file.getSize();
                        File localFile = new File(downloadDir + File.separator + remoteFileName);
                        downloadWithRetry(ftp, remoteFileName, remoteFileLength, localFile);
                    }
                }
            }
        }
        return find;
    }

    /**
     * Retrieve file from FTP server.
     */
    public static void downloadWithRetry(FTPClient ftp, String remoteFileName, long remoteFileLength, File localFile) throws Exception {
        LOG.info("Began to download [{}] ...", remoteFileName);
        FileOutputStream localFOS = null;
        boolean redo = true;
        int retry = 0;
        while (redo) {
            redo = false;
            try {
                localFOS = new FileOutputStream(localFile);
                ftp.retrieveFile(remoteFileName, localFOS);
            } finally {
                if (localFOS != null) {
                    localFOS.close();
                }
                long len = localFile.length();
                if (len == 0 || len != remoteFileLength) {
                    LOG.warn("Remote file length is {}, local file length is {}", remoteFileLength, len);
                    localFile.delete();
                    if (++retry <= RELOAD_MAX_TRY) {
                        redo = true;
                        LOG.warn("Prepared to redownload {} time(s)...", retry);
                        Thread.sleep(RELOAD_WAIT_TIME);
                    }
                } else {
                    LOG.info("Finished download [{}].", remoteFileName);
                }
            }
        }
    }

    private static void closeFTP(FTPClient ftp) {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
    }

    private static class RedoWrapper {
        int redoCount = 0;
        boolean needRedo = true;
    }
}
