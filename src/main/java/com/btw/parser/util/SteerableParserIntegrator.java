package com.btw.parser.util;

import com.btw.parser.mapper.ParserLogMapper;
import com.fate.file.parse.DBSteerableConfig;
import com.fate.file.parse.batch.BatchInsertDB;
import com.fate.file.parse.batch.BatchPool;
import com.fate.file.parse.batch.SteerableBatchMethod;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.file.parse.steerable.FieldSpecification;
import com.fate.file.transfer.FileSelector;
import com.fate.file.utils.FileComparator;
import com.fate.log.ParserLoggerProxy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
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

    public boolean openDownload;

    public boolean openDecompress;

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
            this.openDecompress = info.get("OPEN_DECOMPRESS").toString().equals("Y");
            this.openDownload = info.get("OPEN_DOWNLOAD").toString().equals("Y");
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

    /**
     * 单文件解压解析流程绑定
     * @param lineProcessor
     * @param delRar
     * @param delCsv
     * @throws Exception
     */
    public void unrarAndParse(BatchPool<Map<String , FieldSpecification>> pool, LineProcessor lineProcessor, boolean delRar, boolean delCsv) throws Exception{
        File[] files = FileComparator.sort(saveDir);
        Parser parser = new Parser();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    String order = fileSelector.getOrder(name);
                    if (fileSelector.acceptFile(name) && fileSelector.acceptOrder(order)) {
                        String osName = System.getProperty("os.name");
                        if(osName.toLowerCase().contains("windows")){
                            Unrar5.window(file.getPath(), this.unzipDir, "G:\\WinRar\\WinRAR.exe");
                        } else {
                            Unrar5.linux(file.getPath(), this.unzipDir);
                        }
                        Thread.sleep(3000);
                        File newFile = new File(this.unzipDir + File.separator + name.replace("rar", "csv"));
                        if(logMapper == null) {
                            parser.doWith(newFile, pool, lineProcessor);
                        } else {
                            IParser iParser = new ParserLoggerProxy(logMapper, this.fileType, name, parser).getTarget();
                            iParser.doWith(newFile, pool, lineProcessor);
                        }
                        config.updateOrder(fileType, order);
                        if (delRar) {
                            file.delete();
                        }
                        if(delCsv) {
                            newFile.delete();
                        }
                    }
                }
            }
        }
    }

    public void parse(BatchPool<Map<String , FieldSpecification>> pool, LineProcessor lineProcessor, boolean delCsv) throws Exception{
        File[] files = FileComparator.sort(unzipDir);
        Parser parser = new Parser();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    String order = fileSelector.getOrder(name);
                    if (fileSelector.acceptFile(name) && fileSelector.acceptOrder(order)) {
                        if(logMapper == null) {
                            parser.doWith(file, pool, lineProcessor);
                        } else {
                            IParser iParser = new ParserLoggerProxy(logMapper, this.fileType, name, parser).getTarget();
                            iParser.doWith(file, pool, lineProcessor);
                        }
                        config.updateOrder(fileType, order);
                        if(delCsv) {
                            file.delete();
                        }
                    }
                }
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
            return getBatchInsert(700);
        }

        public BatchPool<Map<String, FieldSpecification>> getBatchInsert(Map<String, FieldSpecification> map, int batchSize){
            BatchInsertDB<Map<String, FieldSpecification>> batchInsertDB = new SteerableBatchMethod(tableName, map, jdbcTemplate);
            BatchPool<Map<String, FieldSpecification>>  pool = new BatchPool<>(tableName, batchInsertDB, batchSize);
            pool.init(map);
            return pool;
        }

        public BatchPool<Map<String, FieldSpecification>> getBatchInsert(int batchSize) {
            return config.createBatchPool(tableName, batchSize);
        }

        public void insertOne(Map<String, FieldSpecification> fieldSpecifications) throws DataAccessException {
            try {
                String sql = config.insertSqlGenerator(tableName, fieldSpecifications);
                jdbcTemplate.update(sql);
            }catch (DuplicateKeyException ignore) {

            }
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

