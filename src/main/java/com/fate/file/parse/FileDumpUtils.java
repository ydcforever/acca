package com.fate.file.parse;

import com.fate.file.parse.processor.FileProcessor;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.file.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;

/**
 * Created by ydc on 2019/11/14.
 */
public final class FileDumpUtils {

    public static void dump(String path, LineProcessor<BufferedWriter> lineProcessor, String storagePath) {
        File file = new File(path);
        dump(file, lineProcessor, storagePath);
    }

    public static void dump(File file, LineProcessor<BufferedWriter> lineProcessor, String storagePath) {
        BufferedWriter bufferedWriter = FileUtil.getWriter(storagePath);
        try {
            FileProcessor.getInstance().process(file, lineProcessor, bufferedWriter);
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
