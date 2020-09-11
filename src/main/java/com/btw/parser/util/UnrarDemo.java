package com.btw.parser.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UnrarDemo {

    public static void main(String[] args) {
        String filepath = "itaxCoupon_Refund20200817.rar";
        int exitCode = doUnRar(filepath);
        if (exitCode !=0 ){
            System.out.println("Error");
        } else {
            System.out.println("Success");
        }
        System.exit(0);
    }

    private static int doUnRar(String filepath) {
        String cmd = String.format("unrar e -y %s", filepath);
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
