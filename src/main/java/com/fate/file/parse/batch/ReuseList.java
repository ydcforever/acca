package com.fate.file.parse.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 支持常规插入操作
 * 扩展无对象插入
 * @param <T>
 */
public final class ReuseList<T> implements Serializable {

    private static final long serialVersionUID = -821625023673947764L;

    private static final Logger LOG = LoggerFactory.getLogger(ReuseList.class);

    private List<T> list;

    private int batchSize = 500;

    private String tableName;

    private BatchInsertDB<T> insertDB;

    public ReuseList(String tableName, BatchInsertDB<T> insertDB) {
        this.list = new ArrayList<T>();
        this.tableName = tableName;
        this.insertDB = insertDB;
    }

    public ReuseList(String tableName, BatchInsertDB<T> insertDB, int batchSize) {
        this.list = new ArrayList<>();
        this.tableName = tableName;
        this.batchSize = batchSize;
        this.insertDB = insertDB;
    }

    public void add(T t) throws Exception {
        if (this.list.size() >= this.batchSize) {
            LOG.info("[{}] meet the batch size and begin to Insert.", tableName);
            boolean success = insertDB.doWith(tableName, list);
            if (success) {
                LOG.info("[{}] complete batch insert.");
            } else {
                throw new Exception("Error---------" + tableName + " BatchInsert Failure!");
            }
            list.clear();
        }
        list.add(t);
    }

    public void restInsert() throws Exception{
        int size = list.size();
        if (size > 0) {
            LOG.info("Begin to insert " + tableName + " test record; total:" + size);
            boolean success = insertDB.doWith(tableName, list);
            if (success) {
                LOG.info("Complete insert [{}] rest record." , tableName);
            } else {
                LOG.error("Rest record of [{}] insert failure!", tableName);
                throw new Exception("Error---------" + tableName + " restInsert Failure!");
            }
        }
    }
}

