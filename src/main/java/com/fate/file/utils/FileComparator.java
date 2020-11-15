package com.fate.file.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by ydc on 2020/11/15.
 */
public final class FileComparator implements Comparator<File> {

    private FileComparator() {

    }

    private static class Builder {
        private static final FileComparator INSTANCE = new FileComparator();
    }

    public static FileComparator getInstance() {
        return Builder.INSTANCE;
    }

    public static File[] sort(String dir){
        File[] files = new File(dir).listFiles();
        Arrays.sort(files, getInstance());
        return files;
    }

    @Override
    public int compare(File o1, File o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
