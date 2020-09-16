package com.btw.parser.util;

import com.btw.parser.mapper.ParserLogMapper;
import com.fate.decompress.DecompressFactory;
import com.fate.decompress.DecompressFile;
import com.fate.decompress.ReaderHandler;
import com.fate.decompress.UnrarFile;
import com.fate.file.parse.DBSteerableConfig;
import com.fate.file.parse.batch.BatchPool;
import com.fate.file.parse.processor.FileProcessor;
import com.fate.file.parse.processor.IFileProcessor;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.file.parse.steerable.FieldSpecification;
import com.fate.file.transfer.FileSelector;
import com.fate.log.ParserLoggerProxy;
import com.github.junrar.Junrar;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by ydc on 2019/12/16.
 * 底层没有约束config的来源，无论是写硬码还是写配置文件或者读库都由用户决定。
 * SteerableParseIntegrator由用户自配，这里只是提供一些集成模板
 */
public class SteerableParserIntegrator {

    private JdbcTemplate jdbcTemplate;

    private String fileType;

    private String splitType;

    private String saveDir;

    private String unzipDir;

    private FileSelector fileSelector;

    private FTPFactory ftpFactory;

    private boolean valid = true;

    private DBSteerableConfig config;

    private ParserLogMapper logMapper = null;

    public SteerableParserIntegrator() {
    }

    public SteerableParserIntegrator(JdbcTemplate jdbcTemplate, String fileType) {
        this.jdbcTemplate = jdbcTemplate;
        this.fileType = fileType;
        this.config = new DBSteerableConfig(jdbcTemplate);
        Map<String, Object> info = config.queryFileStorage(fileType);
        if (info == null) {
            valid = false;
        } else {
            this.ftpFactory = new FTPFactory(info);
            this.splitType = info.get("PARSE_TYPE").toString();
            this.saveDir = info.get("SAVE_DIR").toString();
            Object unDir = info.get("UNZIP_DIR");
            if (unDir != null) {
                this.unzipDir = unDir.toString();
                checkDir(this.unzipDir);
            }
            this.fileSelector = new FileSelector(info.get("FEATURE").toString(), info.get("REGEXP").toString());
            Object begin = info.get("BEGIN_FLAG");
            Object end = info.get("END_FLAG");
            if (begin != null) {
                this.fileSelector.begin(begin.toString());
            }
            if (end != null) {
                this.fileSelector.end(end.toString());
            }
        }
    }

    public SteerableParserIntegrator logMapper(ParserLogMapper logMapper) {
        this.logMapper = logMapper;
        return this;
    }

    public String getUnzipDir() {
        return unzipDir;
    }

    public void download() throws Exception {
        ftpFactory.download(fileSelector);
    }

    //------------------------------group 1 start--------------------------------
    public void unrarFile(boolean delete) throws Exception {
        File[] files = new File(saveDir).listFiles();
        if (files != null) {
            Arrays.sort(files);
            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    String order = fileSelector.getOrder(name);
                    if (fileSelector.acceptFile(name) && fileSelector.acceptOrder(order)) {
                        Junrar.extract(file.getPath(), this.unzipDir);
//                        Unrar5.window(file.getPath(), this.unzipDir, "");
                        if (delete) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    public <T> void parse(LineProcessor<T> lineProcessor, boolean delete) throws Exception {
        parse(this.unzipDir, lineProcessor, delete);
    }

    public <T> void parse(String dir, LineProcessor<T> lineProcessor, boolean delete) throws Exception {
        File[] files = new File(dir).listFiles();
        assert files != null;
        for (File file : files) {
            String name = file.getName();
            String order = fileSelector.getOrder(name);
            if (fileSelector.acceptFile(name) && fileSelector.acceptOrder(order)) {
                IFileProcessor fileProcessor = new ParserLoggerProxy(logMapper, this.fileType, name, FileProcessor.getInstance()).getTarget();
                fileProcessor.process(file, lineProcessor, 0, null);
                config.updateOrder(fileType, order);
                if (delete) {
                    file.delete();
                }
            }
        }
    }
    //-----------------------------------group 1 end--------------------------------


    public void unrarNoFile(ReaderHandler handler) throws Exception {
        unrarNoFile(handler, true);
    }

    public void unrarNoFile(ReaderHandler handler, boolean delete) throws Exception {
        File[] files = new File(saveDir).listFiles();
        if (files != null) {
            Arrays.sort(files);
//            WeChatFare weChatFare = new WeChatFare(logMapper);
            DecompressFile unrarFile = new UnrarFile("GBK");
            for (File file : files) {
                String name = file.getName();
                String order = fileSelector.getOrder(name);
                if (fileSelector.acceptFile(name) && fileSelector.acceptOrder(order)) {
                    DecompressFile decompressFile = new ParserLoggerProxy(logMapper, this.fileType, name, unrarFile)
//                            .parserSend(weChatFare)
                            .getTarget();
                    decompressFile.doWith(file, "", handler);
                    config.updateOrder(fileType, order);
                    if (delete) {
                        file.delete();
                    }
                }
            }
        }
    }

    //本地测试
    public void unrarNoFileNoLog(ReaderHandler handler) throws Exception {
        File[] files = new File(saveDir).listFiles();
        if (files != null) {
            Arrays.sort(files);
            DecompressFile unrarFile = new UnrarFile("GBK");
            DecompressFactory factory = new DecompressFactory(unrarFile, handler);
            for (File file : files) {
                String name = file.getName();
                if (fileSelector.acceptFile(name)) {
                    factory.decompressNoFile(file);
                }
            }
        }
    }

    //本地测试
    public <T> void parseNoLog(String dir, LineProcessor<T> lineProcessor) throws Exception {
        File[] files = new File(dir).listFiles();
        assert files != null;
        for (File file : files) {
            String name = file.getName();
            if (fileSelector.acceptFile(name)) {
                FileProcessor.getInstance().process(file, lineProcessor);
            }
        }
    }

    public SteerableParserIntegrator fileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getSplitType() {
        return splitType;
    }

    public boolean isValid() {
        return valid;
    }

    private void checkDir(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
    }

    /**
     * 单文件可能对应多表，需要多个insert对象
     */
    public class Insert {

        private String tableName;

        private Map<String, FieldSpecification> specifications;

        public Insert() {
            this.tableName = config.queryTableName(fileType, fileType);
            this.specifications = config.loadTableStruct(fileType);
        }

        public Insert(String contextName) {
            this.tableName = config.queryTableName(fileType, contextName);
            this.specifications = config.loadTableStruct(contextName);
        }

        public BatchPool<Map<String, FieldSpecification>> getBatchInsert() {
            return getBatchInsert(2);
        }

        public BatchPool<Map<String, FieldSpecification>> getBatchInsert(int batchSize) {
            return config.createBatchPool(tableName, batchSize);
        }

        public void insertOne(Map<String, FieldSpecification> fieldSpecifications) throws DataAccessException {
            String sql = config.insertSqlGenerator(tableName, fieldSpecifications);
            jdbcTemplate.update(sql);
        }

        public void insertOneWithUpdate(Map<String, FieldSpecification> fieldSpecifications) throws DataAccessException {
            String sql = config.insertSqlGenerator(tableName, fieldSpecifications);
            try {
                jdbcTemplate.update(sql);
            } catch (DuplicateKeyException e) {
                String updateSql = config.updateSqlGenerator(tableName, fieldSpecifications);
                jdbcTemplate.update(updateSql);
            }
        }

        public Map<String, FieldSpecification> getFieldSpecification() {
            return specifications;
        }
    }
}

