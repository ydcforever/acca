package com.fate.decompress;

import com.fate.file.parse.processor.LineProcessor;

import java.io.BufferedReader;

/**
 * Created by ydc on 2020/7/15.
 */
public class NoFileReaderHandler<T> extends ReaderHandler {

    private LineProcessor<T> lineProcessor;

    private T t = null;

    public NoFileReaderHandler(LineProcessor<T> lineProcessor) {
        this.lineProcessor = lineProcessor;
    }

    public NoFileReaderHandler(LineProcessor<T> lineProcessor, T t) {
        this.lineProcessor = lineProcessor;
        this.t = t;
    }

    /**
     *
     * @param filename
     * @param saveDir 无用
     * @param reader
     * @throws Exception
     */
    @Override
    public void doReader(String filename, String saveDir, BufferedReader reader) throws Exception {
        int lineNo = 1;
        String line;
        while((line = reader.readLine()) != null){
            lineProcessor.doWith(line, lineNo, filename, t);
            lineNo++;
        }
    }
}
