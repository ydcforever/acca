package com.fate.decompress;

import org.junit.Test;

import java.io.File;

/**
 * Created by ydc on 2020/12/17.
 */
public class DecompressFactoryTest {

    @Test
    public void decompress() throws Exception {
        DecompressFile decompressFile = new UnrarFile();
        DecompressFactory factory = new DecompressFactory(decompressFile, new NormalReaderHandler());
        factory.decompress(new File("G:\\btw\\acca\\unrar\\D_DP_SAL_20190505.rar"), "G:\\btw\\acca\\unrar");
    }

}