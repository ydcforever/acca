package com.fate.file.parse.steerable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by ydc on 2020/7/27.
 */
public abstract class AbstractSteerableConfig {

    public abstract Map<String, Object> queryFileStorage(String fileType);

    public abstract String queryTableName(String fileType, String contextName);

    public abstract void updateOrder(String fileType, String order);

    public abstract  Map<String, FieldSpecification> loadTableStruct(String contextName);

    public String insertSqlGenerator(String tableName, Map<String, FieldSpecification> row) {
        StringBuilder colBuilder = new StringBuilder();
        StringBuilder valBuilder = new StringBuilder();
        int i = 0;
        Collection<FieldSpecification> collection = row.values();
        for (FieldSpecification field : collection) {
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
        return "INSERT INTO " + tableName + " (" + colBuilder.toString() + ")" +
                " VALUES (" + valBuilder.toString() + ") ";
    }

    public String batchInsertGenerator(String tableName, List<Map<String, FieldSpecification>> rows) {
        StringBuilder builder = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        Collection<FieldSpecification> collection = rows.get(0).values();
        int i = 0;
        StringBuilder colBuilder = new StringBuilder();
        for(FieldSpecification field : collection) {
            if (i > 0) {
                colBuilder.append(",");
            }
            colBuilder.append(field.getCol());
            i++;
        }
        builder.append(colBuilder).append(") ");
        int j = 0;
        for(Map<String, FieldSpecification> row : rows) {
            if(j > 0) {
                builder.append(" UNION ALL ");
            }
            builder.append(selectDual(row));
            j++;
        }
        return builder.toString();
    }

    private StringBuilder selectDual(Map<String, FieldSpecification> row){
        StringBuilder valBuilder = new StringBuilder("SELECT ");
        Collection<FieldSpecification> collection = row.values();
        int i= 0;
        for(FieldSpecification field : collection) {
            if (i > 0) {
                valBuilder.append(",");
            }
            valBuilder.append(valGenerator(field));
            i++;
        }
        return valBuilder.append(" FROM DUAL ");
    }

    public String updateSqlGenerator(String tableName, Map<String, FieldSpecification> row) {
        StringBuilder setBuilder = new StringBuilder(" set ");
        StringBuilder whereBuilder = new StringBuilder(" where ");
        int ki = 0;
        int vi = 0;
        Collection<FieldSpecification> collection = row.values();
        for (FieldSpecification field : collection) {
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
