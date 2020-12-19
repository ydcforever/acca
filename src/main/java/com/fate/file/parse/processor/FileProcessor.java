package com.fate.file.parse.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public final class FileProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FileProcessor.class);

    private static final int BUFFER_SIZE = 64 * 1024;

    public static <T> void process(File file, LineProcessor<T> lineProcessor) throws Exception {
        process(file, lineProcessor, null);
    }

    public static <T> void process(File file, LineProcessor<T> lineProcessor, T global) throws Exception {
        process(file, lineProcessor, 0, global);
    }

    public static void process(File file, ReaderProcessor readerProcessor) throws Exception {
        if (file.length() > 0) {
            FileInputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                String fileName = file.getName();
                inputStream = new FileInputStream(file);
                String charset = readerProcessor.getCharset();
                InputStreamReader reader = charset == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charset);
                bufferedReader = new BufferedReader(reader, BUFFER_SIZE);
                LOG.info("------------------Begin to process file [{}] ", fileName);
                readerProcessor.doWith(bufferedReader, fileName);
                LOG.info("------------------Complete process file [{}]", fileName);
            } finally {
                releaseIOResource(inputStream, bufferedReader);
            }
        }
    }

    public static <T> void process(File file, LineProcessor<T> lineProcessor, int interruptLineNo, T global) throws Exception {
        if (file.length() > 0) {
            String line;
            int lineNo = 1;
            FileInputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                String fileName = file.getName();
                inputStream = new FileInputStream(file);
                String charset = lineProcessor.getCharset();
                InputStreamReader reader = charset == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, charset);
                bufferedReader = new BufferedReader(reader, BUFFER_SIZE);
                LOG.info("------------------Begin to process file [{}] ", fileName);
                while ((line = bufferedReader.readLine()) != null) {
                    try {
                        if (lineNo >= interruptLineNo) {
                            lineProcessor.doWith(bufferedReader, line, lineNo, fileName, global);
                        }
                        lineNo++;
                    } catch (Exception e) {
                        LOG.error("the [{}] at {} line parse failure!", fileName, lineNo);
                        throw new Exception(fileName + "->" + lineNo + ":" + line + " parse failure! " + e.getMessage());
                    }
                }
                LOG.info("------------------Complete process file [{}]", fileName);
            } finally {
                releaseIOResource(inputStream, bufferedReader);
            }
        }
    }

    private static void releaseIOResource(FileInputStream inputStream, BufferedReader bufferedReader) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
        }
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception ignored) {
            }
        }
    }
}
