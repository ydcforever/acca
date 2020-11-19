package com.fate.file.parse;

import com.fate.file.parse.batch.BatchInsertDB;
import com.fate.file.parse.batch.BatchPool;
import com.fate.file.parse.steerable.AbstractSteerableConfig;
import com.fate.file.parse.steerable.FieldSpecification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ydc on 2020/1/14.
 */
public final class DBSteerableConfig extends AbstractSteerableConfig {
    private static final String QUERY_FILE_STORAGE = "select t.addr_type, t.server_dir, t.save_dir, t.unzip_dir, " +
            "t.begin_flag, t.end_flag, t.parse_type, t.feature, t.regexp, f.addr, f.port, f.user_name," +
            " f.pswd,t.open_download, t.open_decompress " +
            "  from PARSER_FILE_STORAGE t" +
            "  left join PARSER_FTP_INFO f" +
            "    on t.addr_id = f.addr_id" +
            "   where t.file_type = ? and t.use_limit = 'Y'";

    private static final String QUERY_PARSER_CONFIG = "select t.db_col, t.data_type, t.is_kv, t.df, t.split_pos, t.start_pos, t.end_pos, t.len " +
            "from PARSER_TABLE_CONFIG t where t.use_limit = 'Y' and t.content_name = ?";

    private static final String QUERY_TABLE_NAME = "select t.tbl_name from PARSER_TABLE_NAME t where t.file_type = ? and t.content_name = ?";

    private static final String UPDATE_ORDER = "update PARSER_FILE_STORAGE t set t.begin_flag = ?, t.end_flag=''" +
            "where t.file_type = ? and t.begin_flag < ?";

    private JdbcTemplate jdbcTemplate;

    public DBSteerableConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> queryFileStorage(String fileType) {
        try {
            return jdbcTemplate.queryForMap(QUERY_FILE_STORAGE, fileType);
        } catch (Exception ignored) {

        }
        return null;
    }

    @Override
    public String queryTableName(String fileType, String contextName) {
        return jdbcTemplate.queryForObject(QUERY_TABLE_NAME, String.class, fileType, contextName);
    }

    @Override
    public void updateOrder(String fileType, String order) {
        jdbcTemplate.update(UPDATE_ORDER, order, fileType, order);
    }

    @Override
    public Map<String, FieldSpecification> loadTableStruct(String contextName) {
        final Map<String, FieldSpecification> map = new LinkedHashMap<>();
        jdbcTemplate.query(QUERY_PARSER_CONFIG, new Object[]{contextName}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                do {
                    FieldSpecification field = new FieldSpecification(rs.getString(1),
                            rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5),
                            rs.getInt(6), rs.getInt(7), rs.getInt(8));
                    map.put(rs.getString(1), field);
                } while (rs.next());
            }
        });
        return map;
    }

    public BatchPool<Map<String, FieldSpecification>> createBatchPool(String tableName, int batchSize) {
        BatchInsertDB<Map<String, FieldSpecification>> insertDB = new BatchInsertDB<Map<String, FieldSpecification>>() {
            @Override
            public void doWith(String tableName, List<Map<String, FieldSpecification>> list) throws Exception {
                String sql = batchInsertGenerator(tableName, list);
                jdbcTemplate.update(sql);
            }
        };
        return new BatchPool<>(tableName, insertDB, batchSize);
    }
}
