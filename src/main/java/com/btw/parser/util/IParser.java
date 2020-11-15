package com.btw.parser.util;

import com.fate.file.parse.batch.BatchPool;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.file.parse.steerable.FieldSpecification;

import java.io.File;
import java.util.Map;

/**
 * Created by ydc on 2020/10/30.
 */
public interface IParser {

    void doWith(File file, BatchPool<Map<String, FieldSpecification>> pool, LineProcessor lineProcessor) throws Exception;

}
