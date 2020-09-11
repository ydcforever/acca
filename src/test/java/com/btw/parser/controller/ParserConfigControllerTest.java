package com.btw.parser.controller;

import com.btw.parser.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class ParserConfigControllerTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String UPDATE_SQL = "update PARSER_FILE_STORAGE t set t.begin_flag = ?, t.end_flag = ?" +
            "where t.file_type = ?";

    private static final String QUERY_SQL = "select t.begin_flag, t.end_flag from PARSER_FILE_STORAGE t where t.file_type = ?";

    @Test
    public void testQuery() throws Exception {
        Map<String, Object> map = jdbcTemplate.queryForMap(QUERY_SQL, new Object[]{"D_DP_SL"});
        String s = JsonUtil.map2json(map);
        System.out.println(s);
    }

    @Test
    public void testUpdate() throws Exception {
        jdbcTemplate.update(UPDATE_SQL, new Object[]{"20190400", "20190402", "D_DP_SAL"});
    }
}