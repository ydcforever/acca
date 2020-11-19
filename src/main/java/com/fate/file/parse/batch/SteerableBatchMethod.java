package com.fate.file.parse.batch;

import com.fate.file.parse.steerable.FieldSpecification;
import com.fate.file.parse.steerable.FieldType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ydc on 2020/11/17.
 */
public class SteerableBatchMethod implements BatchInsertDB<Map<String, FieldSpecification>>{

    private Map<String, FieldSpecification> struct;

    private Map<Integer, String> mapping;

    private JdbcTemplate jdbcTemplate;

    private String sql;

    private String tableName;

    public SteerableBatchMethod(String tableName, Map<String, FieldSpecification> struct, JdbcTemplate jdbcTemplate) {
        this.struct = struct;
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
        init();
    }

    private void init(){
        Collection<FieldSpecification> fieldSpecifications = struct.values();
        mapping = new HashMap<>(struct.size());
        Integer integer = 1;
        StringBuilder colBuilder = new StringBuilder();
        StringBuilder valBuilder = new StringBuilder();
        for(FieldSpecification field : fieldSpecifications){
            mapping.put(integer, field.getCol());
            if(integer > 1){
                colBuilder.append(",");
                valBuilder.append(",");
            }
            colBuilder.append(field.getCol());
            if(FieldType.D.compareTo(field.getType())){
                valBuilder.append("to_date( ").append("?").append(",").append(field.getDf()).append(")");
            } else {
                valBuilder.append("?");
            }
            integer++;
        }
        sql = "insert into " + tableName + " ( " + colBuilder + " ) " + " values (" + valBuilder + " ) ";
    }

    @Override
    public void doWith(String tableName, List<Map<String, FieldSpecification>> list) throws Exception {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String, FieldSpecification> row = list.get(i);
                for(int k = 1, len = row.size(); k <= len; k++){
                    FieldSpecification field = row.get(mapping.get(k));
                    setVal(ps, k, field.getType(), field.getVal());
                }
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    private void setVal(PreparedStatement ps, int pos, String type, String val) throws SQLException {
        if (FieldType.N.compareTo(type)) {
            String num = StringUtils.isEmpty(val.trim()) ? "0" : val;
            ps.setBigDecimal(pos, new BigDecimal(num));
        } else {
            ps.setString(pos, val);
        }
    }
}
