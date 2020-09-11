package com.fate.decompress;

import java.io.File;

/**
 * Created by ydc on 2020/7/15.
 */
public interface DecompressFile {

    void doWith(File file, String saveDir, ReaderHandler handler) throws Exception;

}
