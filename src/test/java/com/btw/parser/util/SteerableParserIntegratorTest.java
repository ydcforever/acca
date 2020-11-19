package com.btw.parser.util;

import com.btw.parser.mapper.ParserLogMapper;
import com.fate.decompress.*;
import com.fate.file.parse.batch.BatchPool;
import com.fate.file.parse.processor.FileProcessor;
import com.fate.file.parse.processor.LineProcessor;
import com.fate.file.parse.steerable.FieldSpecification;
import com.fate.log.ParserLoggerProxy;
import com.github.junrar.Junrar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SteerableParserIntegratorTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ParserLogMapper parserlogMapper;

    //完整测试
    @Test
    public void testUnrarNoFile() throws Exception {

//        AccaUtils.parser("D_IP_OPRA", "ACCA_OPRA_D", jdbcTemplate, parserlogMapper);
        AccaUtils.parser("D_DP_SAL", "ACCA_SAL", jdbcTemplate, parserlogMapper, false);
//        AccaUtils.parser("M_IP_SAL", "ACCA_SAL", jdbcTemplate, parserlogMapper);
//          AccaUtils.parser("D_IP_PRA", "ACCA_PRA_D", jdbcTemplate, parserlogMapper, false);
    }

    @Test
    public void testUnrar() throws Exception {
        String filePath = "C:\\Users\\T440\\Desktop\\beans\\acca\\D_DP_UPL_20190401.rar";
        Junrar.extract(filePath, "C:\\Users\\T440\\Desktop\\beans\\unzip");
    }

    @Test
    public void testCsvParser() throws Exception {
        String filePath = "C:\\Users\\T440\\Desktop\\beans\\unzip\\D_DP_UPL_20190401.csv";
        FileProcessor.process(new File(filePath), new LineProcessor() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                System.out.println(line);
            }
        });
    }

    @Test
    @Deprecated
    public void testLog() throws Exception{
        SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, "M_DP_TAX").logMapper(parserlogMapper);
        final SteerableParserIntegrator.Insert config = integrator.new Insert("ACCA_TAX_DP");
        ReaderHandler handler = new NoFileReaderHandler<>(new LineProcessor<Object>() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                System.out.println(line);
            }
        });
        DecompressFile decompressFile = new ParserLoggerProxy(parserlogMapper, "M_DP_TAX", "M_DP_TAX_201907_20190811.rar", new UnrarFile("GBK")).getTarget();
        DecompressFactory factory = new DecompressFactory(decompressFile, handler);
        factory.decompressNoFile(new File("C:\\Users\\T440\\Desktop\\beans\\acca\\M_DP_SAL_201907_20190811.rar"));
//        ParserLogger logger = new ParserLogger("M_DP_TAX", "M_DP_TAX_201907_20190811.rar", parserlogMapper);
//        try {
//            UnrarFile unrar = new UnrarFile("GBK");
//            logger.start();
//            unrar.doWith(new File("C:\\Users\\T440\\Desktop\\beans\\acca\\M_DP_TAX_201907_20190811.rar"), "", handler);
//            logger.setStatus("Y");
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.setStatus("N");
//            String message = e.getMessage();
//            logger.setExcp(message);
//        }
    }

    @Test
    public void testParserNoLog() throws Exception {
        SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, "M_IP_SAL");
        final SteerableParserIntegrator.Insert config = integrator.new Insert("ACCA_SAL");
        Map<String, FieldSpecification> map = config.getFieldSpecification();
        BatchPool<Map<String, FieldSpecification>> pool = config.getBatchInsert(1000);
        pool.init(map);
        LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                Map<String, FieldSpecification> row = pool.getBatchRow();
                AccaUtils.splitBySpacer(line, row);
                pool.tryBatch();
            }
        };
        FileProcessor.process("C:\\Users\\T440\\Desktop\\beans\\unzip\\M_IP_SAL_201907_20190811.csv", lineProcessor);
        pool.restBatch();
    }

    @Test
    public void testParserDirNoLog() throws Exception {
        SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, "M_DP_SAL");
        final SteerableParserIntegrator.Insert config = integrator.new Insert("ACCA_SAL");
        Map<String, FieldSpecification> map = config.getFieldSpecification();
        BatchPool<Map<String, FieldSpecification>> pool = config.getBatchInsert(1000);
        pool.init(map);
        LineProcessor<Object> lineProcessor = new LineProcessor<Object>() {
            @Override
            public void doWith(String line, int lineNo, String fileName, Object global) throws Exception {
                Map<String, FieldSpecification> row = pool.getBatchRow();
                AccaUtils.splitBySpacer(line, row);
                pool.tryBatch();
            }
        };
//        integrator.parseNoLog("C:\\Users\\T440\\Desktop\\beans\\unzip", lineProcessor);
        pool.restBatch();
    }


    //月数据提前下载，量比较大，然后手动解压到unzip目录下
    @Test
    public void testDownloadAllMonth() throws Exception {
        String[] keys = new String[]{
                "M_DP_SAL","M_IP_SAL",
                "M_DP_TAX","M_IP_TAX",
                "M_DP_UPL", "M_IP_UPL",
                "M_DP_ADM","M_IP_ADM",
                "M_DP_IWB", "M_IP_IWB",
                "M_IP_MCO",
                "M_DP_REF", "M_IP_REF",
                "M_DP_RFD", "M_IP_RFD",
                "M_DP_XBG", "M_IP_XBG",
                "M_MM_AGT",
                "M_MM_CDS",
                "M_MM_CHI",
                "M_MM_FAB",
                "M_MM_FLI",
                "M_MM_PAH",
                "M_MM_PFA",
                "M_MM_VCN",
        };
        for(String key : keys){
            SteerableParserIntegrator integrator = new SteerableParserIntegrator(jdbcTemplate, key);
            integrator.download();
        }
    }
}