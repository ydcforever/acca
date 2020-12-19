package com.fate.file.charset;

/**
 * Created by ydc on 2020/12/18.
 */
public interface FileCharset {

    default String getCharset() throws Exception {
        return null;
    }

}
