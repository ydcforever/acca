package com.fate.file.parse.processor;

import com.fate.file.charset.FileCharset;

import java.io.BufferedReader;

/**
 * Created by ydc on 2020/11/24.
 */
public interface ReaderProcessor extends FileCharset {

    void doWith(BufferedReader bufferedReader, String fileName) throws Exception;

}
