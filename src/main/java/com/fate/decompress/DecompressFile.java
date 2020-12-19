package com.fate.decompress;

import com.fate.file.charset.FileCharset;

import java.io.File;

/**
 * Created by ydc on 2020/7/15.
 */
public interface DecompressFile extends FileCharset {

    void doWith(File file, String saveDir, ReaderHandler handler) throws Exception;

}
