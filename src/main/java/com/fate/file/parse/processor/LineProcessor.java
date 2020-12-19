package com.fate.file.parse.processor;

import com.fate.file.charset.FileCharset;

import java.io.BufferedReader;

/**
 * Created by ydc on 2019/12/19.
 */
public interface LineProcessor<T> extends FileCharset {
    /**
     *
     * @param line
     * @param lineNo start from 1
     * @param fileName
     * @param global
     * @throws Exception
     */
    void doWith(BufferedReader bufferedReader, String line, int lineNo, String fileName, T global) throws Exception;

}
