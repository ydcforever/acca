package com.fate.decompress;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Created by ydc on 2020/7/15.
 */
public abstract class ReaderHandler {

    public abstract void doReader(BufferedReader bufferedReader, String filename, String saveDir, BufferedReader reader) throws Exception;


    /**
     * release IO
     * @param bufferedWriter
     */
    protected static void closeBufferedWriter(BufferedWriter bufferedWriter) {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
