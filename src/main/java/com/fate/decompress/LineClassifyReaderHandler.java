package com.fate.decompress;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ydc on 2020/7/15.
 */
public class LineClassifyReaderHandler extends ReaderHandler {

    private LineClassifier lineClassifier;

    public LineClassifyReaderHandler() {
        lineClassifier = new LineClassifier() {};
    }

    public LineClassifyReaderHandler(LineClassifier lineClassifier) {
        this.lineClassifier = lineClassifier;
    }

    /**
     *
     * @param filename
     * @param saveDir 文件目录
     * @param reader
     * @throws Exception
     */
    @Override
    public void doReader(BufferedReader bufferedReader, String filename, String saveDir, BufferedReader reader) throws Exception{
        String unzipPrefix = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);
        Map<String, BufferedWriter> bufferedWriterMap = null;
        try {
            bufferedWriterMap = makeBufferedWriters(saveDir, unzipPrefix, extension, lineClassifier.keys());
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                String key = lineClassifier.classify(line, lineNo, lineClassifier.keys());
                BufferedWriter bw = bufferedWriterMap.get(key);
                if (bw != null) {
                    bw.write(line);
                    bw.newLine();
                    lineNo++;
                }
            }
        }  finally {
            closeBufferedWriter(bufferedWriterMap);
        }
    }


    /**
     * release IO
     * @param bufferWriterMap split io
     */
    private static void closeBufferedWriter(Map<String, BufferedWriter> bufferWriterMap) {
        Set<String> keys = bufferWriterMap.keySet();
        for (String key : keys) {
            closeBufferedWriter(bufferWriterMap.get(key));
        }
    }


    /**
     * @param unzipDir    will store all unzip files
     * @param unzipPrefix may be original zip file name with or without date
     * @param extension   is unzip file extension, such as '.txt'
     * @param unzipName   is classify by context feature
     * @return map of BufferedWriter
     * @throws java.io.IOException
     */
    public static Map<String, BufferedWriter> makeBufferedWriters(String unzipDir, String unzipPrefix, String extension, String... unzipName) throws IOException {
        int len = unzipName.length;
        Map<String, BufferedWriter> map = new HashMap<>(len);
        for (String name : unzipName) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(makeUnzipPath(unzipDir, unzipPrefix, name, extension)));
            map.put(name, bw);
        }
        return map;
    }

    /**
     * @param unzipDir    will store all unzip files
     * @param unzipPrefix may be original zip file name with or without date
     * @param fileName    must represent some kind of unzip file
     * @param extension   is unzip file extension, such as '.txt'
     * @return unzip file path
     */
    private static String makeUnzipPath(String unzipDir, String unzipPrefix, String fileName, String extension) {
        String path = unzipDir + File.separator + unzipPrefix + fileName;
        if (extension == null || extension.isEmpty()) {
            return path;
        } else {
            return path + "." + extension;
        }
    }

    public interface LineClassifier {
        /**
         * when a zip file has multiple structures, use keys to divide the zip file into more piece
         * all operations of file can use these key
         *
         * @return context keys
         */
        default String[] keys() {
            return new String[0];
        };

        /**
         * classify line to corresponding structure
         *
         * @param line   is text
         * @param lineNo is sequence
         * @param keys   is context type
         * @return context key
         */
        default String classify(String line, int lineNo, String... keys){
            return line;
        };
    }
}
