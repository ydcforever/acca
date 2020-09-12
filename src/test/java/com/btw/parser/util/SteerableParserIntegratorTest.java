package com.btw.parser.util;

import com.btw.parser.mapper.ParserLogMapper;
import com.fate.decompress.NoFileReaderHandler;
import com.fate.decompress.ReaderHandler;
import com.fate.decompress.UnrarFile;
import com.fate.file.parse.processor.FileProcessor;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.log.ParserLogger;
import com.github.junrar.Junrar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SteerableParserIntegratorTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ParserLogMapper parserlogMapper;

    @Test
    public void testUnrarNoFile() throws Exception {

//        AccaUtils.parser("D_IP_OPRA", "ACCA_OPRA_D", jdbcTemplate, parserlogMapper);
//        AccaUtils.parser("D_IP_SAL", "ACCA_SAL", jdbcTemplate, parserlogMapper);
        AccaUtils.parser("M_DP_TAX", "ACCA_TAX_DP", jdbcTemplate, parserlogMapper);
//          AccaUtils.parser("D_DP_UPL", "ACCA_UPL", jdbcTemplate, parserlogMapper);
    }

    @Test
    public void testUnrar() throws Exception{
        String filePath = "C:\\Users\\T440\\Desktop\\beans\\acca\\D_DP_UPL_20190401.rar";
        Junrar.extract(filePath, "C:\\Users\\T440\\Desktop\\beans\\unzip");
    }

    @Test
    public void testPa() throws Exception{
        String filePath = "C:\\Users\\T440\\Desktop\\beans\\unzip\\D_DP_UPL_20190401.csv";
        FileProcessor.getInstance().process(new File(filePath), new LineProcessor() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                System.out.println(line);
            }
        });
    }

    @Test
    public void testLog() {
        SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, "M_DP_TAX").logMapper(parserlogMapper);
        final SteerableParserIntegrator.Insert config = integrator.new Insert("ACCA_TAX_DP");
        ReaderHandler handler = new NoFileReaderHandler<>(new LineProcessor<Object>() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                System.out.println(line);
            }
        });
//        DecompressFile decompressFile = new ParserLoggerProxy(parserlogMapper, "M_DP_TAX", "M_DP_TAX_201907_20190811.rar", unrar).getTarget();
//        DecompressFactory factory = new DecompressFactory(decompressFile, handler);
//        factory.decompressNoFile(new File("C:\\Users\\T440\\Desktop\\beans\\acca\\M_DP_SAL_201907_20190811.rar"));
        ParserLogger logger = new ParserLogger("M_DP_TAX", "M_DP_TAX_201907_20190811.rar", parserlogMapper);
        try {
            UnrarFile unrar = new UnrarFile("GBK");
            logger.start();
            unrar.doWith(new File("C:\\Users\\T440\\Desktop\\beans\\acca\\M_DP_TAX_201907_20190811.rar"), "", handler);
            logger.setStatus("Y");
        } catch (Exception e) {
            e.printStackTrace();
            logger.setStatus("N");
            String message = e.getMessage();
            logger.setExcp(message);
        }
    }

    @Test
    public void testParser() throws Exception {
        SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, "M_IP_SAL");
        final SteerableParserIntegrator.Insert config = integrator.new Insert("ACCA_SAL");
//        final ReuseList<Map<String, FieldSpecification>> reuseList = config.getBatchInsert();
        LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
//                AccaUtils.splitBySpacer(line, config.getFieldSpecification());
//                reuseList.add(specifications);
            }
        };
        FileProcessor.getInstance().process("C:\\Users\\T440\\Desktop\\beans\\unzip\\M_IP_SAL_201907_20190811.csv", lineProcessor);
//        reuseList.restInsert();
    }

    @Test
    public void testParseNoLog() throws Exception {
        SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, "M_DP_SAL");
        final SteerableParserIntegrator.Insert config = integrator.new Insert("ACCA_SAL");
//        final ReuseList<Map<String, FieldSpecification>> reuseList = config.getBatchInsert();
        LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
//                List<FieldSpecification> specifications = AccaUtils.splitBySpacer(line, config.getFieldSpecification());
//                reuseList.add(specifications);
            }
        };
        integrator.parseNoLog("C:\\Users\\T440\\Desktop\\beans\\unzip", lineProcessor);
//        reuseList.restInsert();
    }
}