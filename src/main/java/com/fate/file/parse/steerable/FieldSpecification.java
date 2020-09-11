package com.fate.file.parse.steerable;

import java.io.Serializable;

/**
 * Created by ydc on 2019/12/17.
 */
public final class FieldSpecification implements Serializable, Cloneable{

    private static final String DEFAULT_TYPE = "S";
    /**
     * 表列名
     */
    private String col;

    /**
     * 表数据类型
     * 支持Date, NUMBER, VARCHAR
     */
    private String type;

    /**
     * 数据库日期格式
     */
    private String df;

    /**
     * K 标记更新的条件列
     * V 标记被更新的列
     */
    private String kv;

    /**
     * 分割符截后的数组位置
     * 从1开始
     */
    private int pos;

    /**
     * 截取起始位 必填
     */
    private int begin;

    /**
     * 截取结束位
     * 和 len 二选一
     */
    private int end;

    /**
     * 截取长度
     * 和 end 二选一
     */
    private int len;

    /**
     * 截取值
     */
    private String val;

    /**
     * 是否自定义对象
     * 不参与解析填充
     */
    private boolean define = false;

    public FieldSpecification() {
    }

    public FieldSpecification(String col, String val) {
        this.col = col;
        this.val = val;
        this.type = DEFAULT_TYPE;
    }

    public FieldSpecification(String col, String val, String type) {
        this.col = col;
        this.val = val;
        this.type = type;
    }

    public FieldSpecification(String col, String type, String kv, String df, int pos, int begin, int end, int len) {
        this.col = col;
        this.type = type;
        this.kv = kv;
        this.df = df;
        this.pos = pos;
        this.begin = begin;
        this.end = end;
        this.len = len;
    }

    public String getCol() {
        return col;
    }

    public FieldSpecification col(String col) {
        this.col = col;
        return this;
    }

    public String getType() {
        return type;
    }

    public FieldSpecification type(String type) {
        this.type = type;
        return this;
    }

    public String getKv() {
        return kv;
    }

    public FieldSpecification kv(String kv) {
        this.kv = kv;
        return this;
    }

    public String getDf() {
        return df;
    }

    public FieldSpecification df(String df) {
        this.df = df;
        return this;
    }

    public int getPos() {
        return pos;
    }

    public FieldSpecification pos(int pos) {
        this.pos = pos;
        return this;
    }

    public int getBegin() {
        return begin;
    }

    public FieldSpecification begin(int begin) {
        this.begin = begin;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public FieldSpecification end(int end) {
        this.end = end;
        return this;
    }

    public int getLen() {
        return len;
    }

    public FieldSpecification len(int len) {
        this.len = len;
        return this;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void setValByPos(String[] value) {
        this.val = value[this.pos - 1];
    }

    public FieldSpecification define(String col) {
        this.col = col;
        this.type = DEFAULT_TYPE;
        this.define = true;
        return this;
    }

    public FieldSpecification define(String col, String type) {
        this.col = col;
        this.type = type;
        this.define = true;
        return this;
    }

    public boolean isDefine() {
        return define;
    }

    @Override
    public String toString() {
        return "FieldSpecification{" +
                "col='" + col + '\'' +
                ", type='" + type + '\'' +
                ", val='" + val + '\'' +
                '}';
    }

    public void clear() {
        this.val = "";
    }

    public FieldSpecification clone() {
        FieldSpecification specification = null;
        try {
            specification = (FieldSpecification) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return specification;
    }
}
