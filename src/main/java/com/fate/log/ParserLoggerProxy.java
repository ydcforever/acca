package com.fate.log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ydc on 2020/8/26.
 */
public final class ParserLoggerProxy implements InvocationHandler {

    private static final String STATUS_SUCCESS = "Y";

    private static final String STATUS_FAILURE = "N";

    private IParserLog parserLog = null;

    private IParserSend parserSend = null;

    private String fileType;

    private String fileName;

    private Object target;

    public ParserLoggerProxy(IParserLog parserLog, String fileType, String fileName, Object object) {
        this.parserLog = parserLog;
        this.fileType = fileType;
        this.fileName = fileName;
        this.target = object;
    }

    public ParserLoggerProxy parserSend(IParserSend parserSend) {
        this.parserSend = parserSend;
        return this;
    }

    public <T> T getTarget() {
        Class clazz = this.target.getClass();
        ClassLoader classLoader = clazz.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ParserLogger logger = null;
        try {
            if (parserLog != null) {
                logger = new ParserLogger(this.fileType, this.fileName, parserLog).start();
            }
            method.invoke(target, args);
            if (logger != null) logger.setStatus(STATUS_SUCCESS);
        } catch (Exception e){
            e.printStackTrace();
            if (logger != null) {
                logger.setStatus(STATUS_FAILURE);
                String message = e.getCause().getMessage();
                logger.setExcp(subMessage(message));
                if(parserSend != null){
                    parserSend.sendMessage(this.fileType, message);
                }
            }
        }
        if (logger != null) {
            logger.end();
        }
        return null;
    }

    public static String subMessage(String message){
        return message.length() > 2000 ? message.substring(0, 2000) : message;
    }
}
