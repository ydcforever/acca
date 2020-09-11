package com.fate.decompress;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.*;
import java.util.List;

/**
 * Created by ydc on 2020/7/16.
 */
public class UnrarFile implements DecompressFile {

    private String charsetName;

    public UnrarFile() {
    }

    public UnrarFile(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    public void doWith(File file, String saveDir, ReaderHandler handler) throws Exception{
        Archive archive = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            archive = new Archive(is);
            List<FileHeader> headers = archive.getFileHeaders();
            if(headers.size() == 0) {
                throw new IOException(file.getName() + " can not unrar! It maybe rar5");
            }
            for(FileHeader fileHeader : headers){
                if (!fileHeader.isDirectory()) {
                    long size = fileHeader.getDataSize();
                    if(size > 0) {
                        InputStream stream = archive.getInputStream(fileHeader);
                        InputStreamReader reader;
                        if (charsetName.equals("") || charsetName == null) {
                            reader = new InputStreamReader(stream);
                        } else {
                            reader = new InputStreamReader(stream, charsetName);
                        }
                        BufferedReader br = new BufferedReader(reader);
                        handler.doReader(fileHeader.getFileNameString(), saveDir, br);
                    }
                }
            }
        } finally {
            releaseResources(is, archive);
        }
    }

    private static void releaseResources(FileInputStream is, Archive archive) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (archive != null) {
                archive.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
