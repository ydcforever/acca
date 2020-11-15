package com.fate.file.parse.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public final class FileProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FileProcessor.class);

    private static final int BUFFER_SIZE = 64 * 1024;

    private FileProcessor() {
    }

    public static void process(String filePath, LineProcessor lineParser) throws Exception {
        process(filePath, lineParser, null);
    }

    public static <T> void process(String filePath, LineProcessor<T> lineParser, T global) throws Exception {
        File file = new File(filePath);
        process(file, lineParser, global);
    }

    public static void process(File file, LineProcessor lineParser) throws Exception {
        process(file, lineParser, null);
    }

    public static <T> void process(File file, LineProcessor<T> lineParser, T global) throws Exception {
        process(file, lineParser, 0, global);
    }

    /**
     * @param file
     * @param lineParser
     * @param interruptLineNo continue when process had interrupt
     * @param global
     * @param <T>
     * @throws Exception
     */
    public static <T> void process(File file, LineProcessor<T> lineParser, int interruptLineNo, T global) throws Exception {
        if (file.length() > 0) {
            String line;
            int lineNo = 1;
            FileInputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                String fileName = file.getName();
                inputStream = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), BUFFER_SIZE);
                LOG.info("------------------Begin to process file [{}] ", fileName);
                while ((line = bufferedReader.readLine()) != null) {
                    try {
                        if (lineNo >= interruptLineNo) {
                            lineParser.doWith(line, lineNo, fileName, global);
                        }
                        lineNo++;
                    } catch (Exception e) {
                        LOG.error("the [{}] at {} line parse failure!", fileName, lineNo);
                        throw new Exception(fileName + "->" + lineNo + " parse failure! " + e.getMessage());
                    }
                }
                LOG.info("------------------Complete process file [{}]", fileName);
            } finally {
                releaseIOResource(inputStream, bufferedReader);
            }
        }
    }

    private static void releaseIOResource(FileInputStream inputStream, BufferedReader bufferedReader) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
        }
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception ignored) {
            }
        }
    }
}
