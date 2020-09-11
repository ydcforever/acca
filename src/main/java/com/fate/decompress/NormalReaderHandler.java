package com.fate.decompress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by ydc on 2020/7/15.
 */
public class NormalReaderHandler extends ReaderHandler {

    /**
     *
     * @param filename
     * @param saveDir 文件名
     * @param reader
     * @throws Exception
     */
    @Override
    public void doReader(String filename, String saveDir, BufferedReader reader) throws Exception {
        BufferedWriter bufferedWriter = null;
        String line;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(saveDir));
            while ((line = reader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } finally {
            closeBufferedWriter(bufferedWriter);
        }
    }

}
