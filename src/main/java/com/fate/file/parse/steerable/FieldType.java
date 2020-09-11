package com.fate.file.parse.steerable;

/**
 * Created by ydc on 2019/12/19.
 */
public enum FieldType {

    S("S"),
    N("N"),
    D("D");

    FieldType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean compareTo(String s) {
        return s.equals(this.type);
    }
}
