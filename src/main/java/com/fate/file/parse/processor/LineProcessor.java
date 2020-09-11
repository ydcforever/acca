package com.fate.file.parse.processor;

/**
 * Created by ydc on 2019/12/19.
 */
public interface LineProcessor<T> {
    /**
     *
     * @param line
     * @param lineNo start from 1
     * @param fileName
     * @param global
     * @throws Exception
     */
   public void doWith(String line, int lineNo, String fileName, T global) throws Exception;
}
