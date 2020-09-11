package com.fate.file.parse.steerable;

/**
 * Created by ydc on 2019/12/17.
 */
public enum SplitType {

    S("S"),
    SE("SE"),
    FSE("FSE"),
    SL("SL"),
    FSL("FSL");

    SplitType(String type) {
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
