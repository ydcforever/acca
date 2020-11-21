package com.fate.file.parse.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ydc on 2020/9/11.
 * Build pool by cloning object step by step when the size of pool no more than batch size.
 * If the pool is no longer in use, it must be destroy
 */
public final class BatchPool<T> implements Serializable, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(BatchPool.class);

    private List<T> pool;

    private T t;

    private int batchSize;

    private int offset = 0;

    private String tableName;

    private BatchMethod<T> batchMethod;

    public BatchPool(String tableName, BatchMethod<T> batchMethod, T t, int batchSize) {
        this.batchSize = batchSize;
        this.tableName = tableName;
        this.batchMethod = batchMethod;
        this.pool = new LinkedList<>();
        this.t = t;
    }

    public T getBatchRow() {
        if(pool.size() < batchSize) {
            T clone = clone(t);
            pool.add(clone);
        }
        int circle = offset % batchSize;
        T t = pool.get(circle);
        offset = circle + 1;
        return t;
    }

    public void tryBatch() throws Exception {
        if (offset == batchSize) {
            LOG.info("[{}] meet the batch size and begin to insert.", tableName);
            batchMethod.insert(pool);
            LOG.info("[{}] complete batch insert.");
        }
    }

    public void restBatch() throws Exception {
        if (offset > 0 && offset < batchSize) {
            LOG.info("Begin to insert " + tableName + " rest record :" + offset);
            try {
                batchMethod.insert(pool.subList(0, offset));
                LOG.info("Complete insert [{}] rest record.", tableName);
            } finally {
                offset = 0;
            }
        }
    }

    private T clone(T obj) {
        T cloneObj = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            cloneObj = (T)ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }

    @Override
    public void destroy() throws Exception {
        pool.clear();
    }
}
