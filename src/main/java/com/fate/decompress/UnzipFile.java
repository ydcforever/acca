package com.fate.decompress;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by ydc on 2020/7/16.
 */
public class UnzipFile implements DecompressFile {

    @Override
    public void doWith(File file, String saveDir, ReaderHandler handler) throws Exception{
        ZipFile zf = null;
        ZipInputStream zis = null;
        ZipEntry ze;
        BufferedReader br = null;
        long realSize;
        try {
            zf = new ZipFile(file);
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
            ze = zis.getNextEntry();
            realSize = ze.getSize();
            if (realSize > 0) {
                br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
                handler.doReader(br, file.getName(), saveDir, br);
            }
        }  finally {
            releaseResources(zf, zis, br);
        }
    }

    /**
     * release IO
     *
     * @param zf
     * @param zis
     * @param br
     */
    private static void releaseResources(ZipFile zf, ZipInputStream zis, BufferedReader br) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (zis != null) {
            try {
                zis.closeEntry();
                zis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (zf != null) {
            try {
                zf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

