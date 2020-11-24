package com.fate.file.parse.processor;

import java.io.BufferedReader;

/**
 * Created by ydc on 2020/11/24.
 */
public interface ReaderProcessor {

    void doWith(BufferedReader bufferedReader, String fileName) throws Exception;

}
