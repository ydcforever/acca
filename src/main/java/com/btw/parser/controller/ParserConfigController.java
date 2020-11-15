package com.btw.parser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by ydc on 2020/8/29.
 */
@Controller
@RequestMapping("/cfg")
public class ParserConfigController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String UPDATE_SQL = "update PARSER_FILE_STORAGE t set t.begin_flag = ?, t.end_flag = ?" +
            "where t.file_type = ?";

    private static final String QUERY_SQL = "select t.begin_flag, t.end_flag from PARSER_FILE_STORAGE t where t.file_type = ?";

    @RequestMapping(value = "/query.do", method = RequestMethod.GET)
    @ResponseBody
    public String query(@RequestParam("id")String id) throws Exception{
        try{
            Map<String, Object> map = jdbcTemplate.queryForMap(QUERY_SQL, new Object[]{id});
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        }catch (Exception e){
        }
        return "";
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestParam("id")String id, @RequestParam("begin")String begin, @RequestParam("end")String end) throws Exception{
        jdbcTemplate.update(UPDATE_SQL, new Object[]{begin, end, id});
    }
}
