package com.fate.decompress;

import com.fate.file.transfer.FileSelector;

import java.io.File;
import java.util.Arrays;

/**
 * Created by ydc on 2020/7/15.
 */
public class DecompressFactory {

    private DecompressFile decompressFile;

    private ReaderHandler readerHandler;

    public DecompressFactory(DecompressFile decompressFile, ReaderHandler readerHandler) {
        this.decompressFile = decompressFile;
        this.readerHandler = readerHandler;
    }

    public void decompressNoFile(String zipDir, FileSelector fileSelector) throws Exception{
        File[] files = new File(zipDir).listFiles();
        if(files != null) {
            Arrays.sort(files);
            for (File file : files) {
                String name = file.getName();
                String order = fileSelector.getOrder(name);
                if(fileSelector.acceptFile(name) && fileSelector.acceptOrder(order)) {
                    decompressNoFile(file);
                }
            }
        }
    }

    public void decompress(String zipDir, String unzipDir, FileSelector fileSelector) throws Exception{
        File[] files = new File(zipDir).listFiles();
        if(files != null) {
            Arrays.sort(files);
            for (File file : files) {
                String name = file.getName();
                String order = fileSelector.getOrder(name);
                if(fileSelector.acceptFile(name) && fileSelector.acceptOrder(order)) {
                    decompress(file, unzipDir);
                }
            }
        }
    }

    public void decompressNoFile(File file) throws Exception{
        decompressFile.doWith(file, "", readerHandler);
    }

    public void decompress(File file, String unzipDir) throws Exception{
        decompressFile.doWith(file, unzipDir, readerHandler);
    }
}
