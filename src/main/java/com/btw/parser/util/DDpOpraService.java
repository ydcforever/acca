package com.btw.parser.util;

import com.btw.parser.mapper.ParserLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by ydc on 2020/11/24.
 */
@Service
public class DDpOpraService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ParserLogMapper parserlogMapper;

    public void doTask(String ftype, String ctxName) throws Exception{
        AccaUtils.parser(ftype, ctxName, jdbcTemplate, parserlogMapper);
    }
}
