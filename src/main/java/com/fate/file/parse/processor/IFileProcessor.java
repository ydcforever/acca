package com.fate.file.parse.processor;

import java.io.File;

/**
 * Created by ydc on 2020/8/27.
 */
public interface IFileProcessor {

    public <T> void process(File file, LineProcessor<T> lineParser, int interruptLineNo, T global) throws Exception;

}
