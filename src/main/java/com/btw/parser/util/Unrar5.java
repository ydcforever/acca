package com.btw.parser.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by ydc on 2020/9/15.
 */
public final class Unrar5 {

    public static int linux(String rarFile, String unrarDir) {
        String cmd = "unrar x " + rarFile + " " + unrarDir;
        return doUnRar(cmd);
    }

    public static int window(String rarFile, String unrarDir, String exePath){
        String cmd = exePath + " x " + rarFile + " " + unrarDir;
        return doUnRar(cmd);
    }

    private static int doUnRar(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            return process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream))
                    .lines().forEach(consumer);
        }
    }
}
