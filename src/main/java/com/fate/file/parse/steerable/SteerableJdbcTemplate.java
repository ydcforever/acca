package com.fate.file.parse.steerable;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ydc on 2020/11/19.
 */
public final class SteerableJdbcTemplate {

    public static void updateSteerable(JdbcTemplate jdbcTemplate, String sql, List<String> assemblingField, Map<String, FieldSpecification> row) {
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int k = 0, len = assemblingField.size(); k < len; k++) {
                    FieldSpecification field = row.get(assemblingField.get(k));
                    assembling(ps, k + 1, field.getType(), field.getVal());
                }
            }
        });
    }

    public static void batchUpdateSteerable(JdbcTemplate jdbcTemplate, String sql, List<String> assemblingField, List<Map<String, FieldSpecification>> list){
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String, FieldSpecification> row = list.get(i);
                for(int k = 0, len = assemblingField.size(); k < len; k++){
                    FieldSpecification field = row.get(assemblingField.get(k));
                    assembling(ps, k + 1, field.getType(), field.getVal());
                }
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    public static String preparedInsertSql(String tableName, Map<String, FieldSpecification> struct, List<String> assemblingField){
        Collection<FieldSpecification> fieldSpecifications = struct.values();
        int k = 0;
        StringBuilder colBuilder = new StringBuilder();
        StringBuilder valBuilder = new StringBuilder();
        for(FieldSpecification field : fieldSpecifications){
            if(k > 0){
                colBuilder.append(",");
                valBuilder.append(",");
            }
            String col = field.getCol();
            assemblingField.add(col);
            colBuilder.append(col);
            valBuilder.append(preparedWildcard(field));
            k++;
        }
        return  "insert into " + tableName + " ( " + colBuilder + " ) " + " values (" + valBuilder + " ) ";
    }

    public static String preparedUpdateSql(String tableName, Map<String, FieldSpecification> struct, List<String> assemblingField){
        StringBuilder setBuilder = new StringBuilder(" set ");
        StringBuilder whereBuilder = new StringBuilder(" where ");
        int ki = 0;
        int vi = 0;
        Collection<FieldSpecification> collection = struct.values();
        List<String> whereAssemblingField = new LinkedList<>();
        for (FieldSpecification field : collection) {
            String kv = field.getKv();
            if("K".equals(kv)) {
                if(ki > 0) {
                    whereBuilder.append(" and ");
                }
                whereAssemblingField.add(field.getCol());
                whereBuilder.append(preparedSetValue(field));
                ki++;
            } else if ("V".equals(kv)) {
                if(vi > 0) {
                    setBuilder.append(", ");
                }
                assemblingField.add(field.getCol());
                setBuilder.append(preparedSetValue(field));
                vi++;
            }
        }
        assemblingField.addAll(whereAssemblingField);
        return "update " + tableName + setBuilder.toString() + whereBuilder.toString();
    }

    private static String preparedSetValue(FieldSpecification field){
        return field.getCol() + " = " + preparedWildcard(field);
    }
    private static String preparedWildcard(FieldSpecification field){
        if(FieldType.D.compareTo(field.getType())){
            return " to_date(? , '" + field.getDf() + "') ";
        } else {
            return " ? ";
        }
    }

    private static void assembling(PreparedStatement ps, int pos, String type, String val) throws SQLException {
        if (FieldType.N.compareTo(type)) {
            String num = StringUtils.isEmpty(val.trim()) ? "0" : val;
            ps.setBigDecimal(pos, new BigDecimal(num));
        } else {
            ps.setString(pos, val);
        }
    }
}
