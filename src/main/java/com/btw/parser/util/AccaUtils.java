package com.btw.parser.util;

import com.btw.parser.mapper.ParserLogMapper;
import com.fate.decompress.NoFileReaderHandler;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.file.parse.steerable.FieldSpecification;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ydc on 2020/8/29.
 */
public final class AccaUtils {

    /**
     * r5解压剥离
     * @param ftype
     * @param ctxName
     * @param jdbcTemplate
     * @param parserlogMapper
     * @throws Exception
     */
//    public static void parser(String ftype, String ctxName, JdbcTemplate jdbcTemplate, ParserLogMapper parserlogMapper) throws Exception {
//        final SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, ftype).logMapper(parserlogMapper);
//        if(integrator.isValid()) {
//            final SteerableParserIntegrator.Insert config = integrator.new Insert(ctxName);
//            final ReuseList<List<FieldSpecification>> reuseList = config.getBatchInsert();
//            LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
//                @Override
//                public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
//                    List<FieldSpecification> specifications = splitBySpacer(line, config.getFieldSpecification());
////                    config.insertOne(specifications);
//                    reuseList.add(specifications);
//                }
//            };
//            integrator.parse(integrator.getUnzipDir(), lineProcessor, false);
//            reuseList.restInsert();
//        }
//    }

    public static void parser(String ftype, String ctxName, JdbcTemplate jdbcTemplate, ParserLogMapper parserlogMapper) throws Exception {
        final SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, ftype).logMapper(parserlogMapper);
        if(integrator.isValid()) {
            integrator.download();
            final SteerableParserIntegrator.Insert config = integrator.new Insert(ctxName);
//            final ReuseList<List<FieldSpecification>> reuseList = config.getBatchInsert();
            LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
                @Override
                public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                    List<FieldSpecification> specifications = splitBySpacer(line, config.getFieldSpecification());
                    specifications.add(new FieldSpecification("SOURCE_NAME", fileName));
                    config.insertOne(specifications);
                    specifications.clear();
//                    reuseList.add(specifications);
                }
            };
            integrator.unrarNoFile(new NoFileReaderHandler<Object>(lineProcessor));
//            reuseList.restInsert();
        }
    }

//    public static void parser(String ftype, String ctxName, JdbcTemplate jdbcTemplate, ParserLogMapper parserlogMapper) throws Exception {
//        final SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, ftype).logMapper(parserlogMapper);
//        if(integrator.isValid()) {
////            integrator.download();
//            integrator.unrarFile(false);
//            final SteerableParserIntegrator.Insert config = integrator.new Insert(ctxName);
//            LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
//                @Override
//                public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
//                    List<FieldSpecification> specifications = splitBySpacer(line, config.getFieldSpecification());
//                    config.insertOne(specifications);
//                    specifications.clear();
//                }
//            };
//            integrator.parse(lineProcessor, true);
//        }
//    }

    public static List<FieldSpecification> splitBySpacer(String line, List<FieldSpecification> specifications) {
        String[] fields = line.split("\",\"");
        int len = fields.length;
        List<FieldSpecification> row = new LinkedList<>();
        for (FieldSpecification specification : specifications) {
            FieldSpecification clone = specification.clone();
            if(clone.getPos() <= len) {
                clone.setVal(fields[clone.getPos() - 1].replace("\"", ""));
                row.add(clone);
            }
        }
        return row;
    }
}
