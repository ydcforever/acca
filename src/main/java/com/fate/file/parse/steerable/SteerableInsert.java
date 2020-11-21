package com.fate.file.parse.steerable;

import com.fate.file.parse.batch.BatchMethod;
import com.fate.file.parse.batch.BatchPool;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ydc on 2020/11/20.
 */
public class SteerableInsert {

    private String tableName;

    private List<String> assemblingInsert;

    private String insertSql;

    private List<String> assemblingUpdate;

    private String updateSql;

    private Map<String, FieldSpecification> struct;

    private JdbcTemplate jdbcTemplate;

    public SteerableInsert(JdbcTemplate jdbcTemplate, String tableName, Map<String, FieldSpecification> struct) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
        this.struct = struct;
        assemblingInsert = new LinkedList<>();
        insertSql = SteerableJdbcTemplate.preparedInsertSql(tableName, struct, assemblingInsert);
    }

    public SteerableInsert preparedUpdateSql(){
        assemblingUpdate = new LinkedList<>();
        updateSql = SteerableJdbcTemplate.preparedUpdateSql(tableName, struct, assemblingUpdate);
        return this;
    }

    public BatchPool<Map<String, FieldSpecification>> createBatchPool(int batchSize) {
        BatchMethod<Map<String, FieldSpecification>> method = new BatchMethod<Map<String, FieldSpecification>>() {
            @Override
            public void insert(List<Map<String, FieldSpecification>> list) throws Exception {
                SteerableJdbcTemplate.batchUpdateSteerable(jdbcTemplate, insertSql, assemblingInsert, list);
            }
        };
        return new BatchPool<>(tableName, method, struct, batchSize);
    }

    public void insertOne(Map<String, FieldSpecification> row) throws DataAccessException {
        SteerableJdbcTemplate.updateSteerable(jdbcTemplate, insertSql, assemblingInsert, row);
    }

    public void insertOneWithUpdate(Map<String, FieldSpecification> row) throws DataAccessException {
        try {
            insertOne(row);
        } catch (DuplicateKeyException e) {
            SteerableJdbcTemplate.updateSteerable(jdbcTemplate, updateSql, assemblingUpdate, row);
        }
    }
}
