package com.fate.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ydc on 2020/8/24.
 */
public class ParserLogger {

    private static final SimpleDateFormat SDF_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String fileType;

    private String fileName;

    private String startTime;

    private String endTime;

    private String status;

    private String excp;

    private IParserLog parserLog;

    public ParserLogger(String fileType, String fileName, IParserLog parserLog) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.parserLog = parserLog;
    }

    public ParserLogger start() {
        this.startTime = SDF_HMS.format(new Date());
        parserLog.insertLog(this);
        return this;
    };

    public void end(){
        this.endTime =  SDF_HMS.format(new Date());
        parserLog.updateLog(this);
    };

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setExcp(String excp) {
        this.excp = excp;
    }

    public String getExcp() {
        return excp;
    }
}
