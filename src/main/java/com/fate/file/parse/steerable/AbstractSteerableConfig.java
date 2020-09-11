package com.fate.file.parse.steerable;

import com.fate.file.parse.batch.BatchInsertDB;
import com.fate.file.parse.batch.ReuseList;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by ydc on 2020/7/27.
 */
public abstract class AbstractSteerableConfig {

    public abstract Map<String, Object> queryFileStorage(String fileType);

    public abstract String queryTableName(String fileType, String contextName);

    public abstract void updateOrder(String fileType, String order);

    public abstract List<FieldSpecification> loadTableStruct(String contextName);

    public ReuseList<List<FieldSpecification>> createReuseList(final JdbcTemplate jdbcTemplate, String tableName, int batchSize) {
        BatchInsertDB<List<FieldSpecification>> insertDB = new BatchInsertDB<List<FieldSpecification>>() {
            @Override
            public boolean doWith(String tableName, List<List<FieldSpecification>> list) {
                try {
                    String sql = batchInsertGenerator(tableName, list);
                    jdbcTemplate.update(sql);
                    return true;
                } catch (DataAccessException ignored) {

                }
                return false;
            }
        };
        return new ReuseList<List<FieldSpecification>>(tableName, insertDB, batchSize);
    }

    public String batchInsertGenerator(String tableName, List<List<FieldSpecification>> rows) {
        StringBuilder insert = new StringBuilder("INSERT ALL");
        for(List<FieldSpecification> row : rows) {
            insert.append(insertOneGenerator(tableName, row));
        }
        insert.append("SELECT 1 FROM DUAL");
        return insert.toString();
    }

    public String insertSqlGenerator(String tableName, List<FieldSpecification> row) {
        return "INSERT" + insertOneGenerator(tableName, row);
    }

    private String insertOneGenerator(String tableName, List<FieldSpecification> row) {
        StringBuilder colBuilder = new StringBuilder();
        StringBuilder valBuilder = new StringBuilder();
        int i = 0;
        for (FieldSpecification field : row) {
            String val = field.getVal();
            if(val != null && !"".equals(val.trim())){
                if (i > 0) {
                    colBuilder.append(",");
                    valBuilder.append(",");
                }
                String col = field.getCol();
                String type = field.getType();
                colBuilder.append(col);
                valBuilder.append(valGenerator(type, val, field.getDf()));
                i++;
            }
        }
        return " INTO " + tableName + " (" + colBuilder.toString() + ")" +
                " VALUES (" + valBuilder.toString() + ") ";
    }

    public String updateSqlGenerator(String tableName, List<FieldSpecification> row) {
        StringBuilder setBuilder = new StringBuilder(" set ");
        StringBuilder whereBuilder = new StringBuilder(" where ");
        int ki = 0;
        int vi = 0;
        for (FieldSpecification field : row) {
            String kv = field.getKv();
            if("K".equals(kv)) {
                if(ki > 0) {
                    whereBuilder.append(" and ");
                }
                whereBuilder.append(setVal(field));
                ki++;
            } else if ("V".equals(kv)) {
                if(vi > 0) {
                    whereBuilder.append(", ");
                }
                setBuilder.append(setVal(field));
                vi++;
            }
        }
        return "UPDATE " + tableName + setBuilder.toString() + whereBuilder.toString();
    }

    public StringBuilder setVal(FieldSpecification fieldSpecification){
        StringBuilder sb = new StringBuilder();
        StringBuilder val = valGenerator(fieldSpecification);
        sb.append(fieldSpecification.getCol()).append(" = ").append(val);
        return sb;
    }

    public StringBuilder valGenerator(FieldSpecification fieldSpecification){
        return valGenerator(fieldSpecification.getType(), fieldSpecification.getVal(), fieldSpecification.getDf());
    }

    private StringBuilder valGenerator(String type, String val, String df) {
        StringBuilder sb = new StringBuilder();
        if (FieldType.D.compareTo(type)) {
            sb.append("to_date('").append(val).append("', '").append(df).append("')");
        } else if (FieldType.N.compareTo(type)) {
            sb.append(val);
        } else {
            sb.append("'").append(val.replace("'","''")).append("'");
        }
        return sb;
    }
}
