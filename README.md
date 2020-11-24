# acca
ACCA数据定时解析

注意事项

a.由于定时操作会有线程并发，使用jdbcTemplate时请勿混用

b.日志代理需要代理接口，自定义解析接口

c.定时任务配置：spring 配置SteerableScheduleManager，web.xml配置监听SteerableScheduleListener

使用说明

1.AccaUtils 提供适应性解析方法

2.SteerableParserIntegrator 提供解析集成方法

3.目前不支持rar5解压，Unrar5提供软件解压

4.com.fate目录下都是jar包源码，可优化。

   4.1 DecompressFactory 集成了rar4,zip的解压，NoFileReaderHandler不生成解压文件

   4.2 FTPAccessor 提供FTP下载功能， FTPFileProcessor接口结合downloadWithRetry可自定义文件过滤

   4.3 BatchPool提供批处理对象池，用完必须destroy

   4.4  SteerableConfig接口定义了动态结构配置加载方法，SteerableInsert 集成了基于动态结构解析的数据库操作，
        SteerableLineProcessUtils提供简单的动态结构行填充方法

   4.5 ParserLoggerProxy 集成了解析日志和通知（日志对象限制了自由度）

   4.6 SteerableScheduleManager 提供启停和修改定时串方法，提供自动开启定时参数，新增tomcat监听以便停止线程


