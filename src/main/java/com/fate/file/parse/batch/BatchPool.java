package com.fate.file.parse.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ydc on 2020/9/11.
 */
public final class BatchPool<T> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchPool.class);

    private List<T> pool;

    private int batchSize;

    private int offset = 0;

    private String tableName;

    private BatchInsertDB<T> insertDB;

    public BatchPool(String tableName, BatchInsertDB<T> insertDB, int batchSize) {
        this.batchSize = batchSize;
        this.tableName = tableName;
        this.insertDB = insertDB;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public BatchPool<T> init(T t){
        pool = new LinkedList<>();
        for(int i=0; i < batchSize; i++) {
            T clone = clone(t);
            pool.add(clone);
        }
        return this;
    }

    public T getBatchRow() {
        int circle = offset % batchSize;
        T t = pool.get(circle);
        offset = circle + 1;
        return t;
    }

    public void tryBatch() throws Exception{
        if(offset == batchSize) {
            insertDB.doWith(tableName, pool);
        }
    }

    public void restBatch() throws Exception{
        if (offset < batchSize) {
            insertDB.doWith(tableName, pool.subList(0, offset));
        }
    }

    private T clone(T obj) {
        T cloneObj = null;
        try {
            // 写入字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();
            // 分配内存，写入原始对象，生成新对象
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            // 返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }
}
