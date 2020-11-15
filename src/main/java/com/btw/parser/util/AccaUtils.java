package com.btw.parser.util;

import com.btw.parser.mapper.ParserLogMapper;
import com.fate.file.parse.batch.BatchPool;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.file.parse.steerable.FieldSpecification;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.Map;

/**
 * Created by ydc on 2020/8/29.
 */
public final class AccaUtils {

    /**
     * 批量插入
     */
    public static void parser(String ftype, String ctxName, JdbcTemplate jdbcTemplate, ParserLogMapper parserlogMapper, boolean rar5) throws Exception {
        final SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, ftype).logMapper(parserlogMapper);
        if(integrator.isValid()) {
            integrator.download();
            final SteerableParserIntegrator.Insert config = integrator.new Insert(ctxName);
            Map<String, FieldSpecification> map = config.getFieldSpecification();
            map.put("SOURCE_NAME", new FieldSpecification().define("SOURCE_NAME"));
            BatchPool<Map<String , FieldSpecification>> pool = config.getBatchInsert(700);
            pool.init(map);
            LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
                @Override
                public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                    Map<String, FieldSpecification> row = pool.getBatchRow();
                    row.get("SOURCE_NAME").setVal(fileName);
                    splitBySpacer(line, row);
                    pool.tryBatch();
                }
            };

            integrator.unrarAndParse(pool, lineProcessor, false, true);

            integrator.parse(pool, lineProcessor, true);

            pool.destroy();
        }
    }

    public static void splitBySpacer(String line, Map<String, FieldSpecification> specifications) {
        String[] fields = line.split("\",\"");
        int len = fields.length;
        Collection<FieldSpecification> collection = specifications.values();
        for (FieldSpecification field : collection) {
            if(!field.isDefine()){
                field.clear();
                if(field.getPos() <= len) {
                    field.setVal(fields[field.getPos() - 1].replace("\"", "").trim());
                }
            }
        }
    }
}
