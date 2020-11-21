package com.fate.file.parse.steerable;

import java.util.Map;

/**
 * Created by ydc on 2020/7/27.
 */
public interface SteerableConfig {

    public abstract Map<String, Object> queryFileStorage(String fileType);

    public abstract String queryTableName(String fileType, String contextName);

    public abstract void updateOrder(String fileType, String order);

    public abstract Map<String, FieldSpecification> loadTableStruct(String contextName);

}
