package com.fate.file.transfer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ydc on 2019/12/18.
 */
public final class FileSelector {

    /**
     * 正则 用于过滤文件
     */
    private String feature;

    /**
     * 正则 用于过滤日期或其他比较项
     */
    private String regexp;

    private String begin;

    private String end;

    public FileSelector(String feature, String regexp) {
        this.feature = feature;
        this.regexp = regexp;
    }

    public FileSelector begin(String begin) {
        this.begin = begin;
        return this;
    }

    public FileSelector end(String end) {
        this.end = end;
        return this;
    }

    public String getFeature() {
        return feature;
    }

    public String getRegexp() {
        return regexp;
    }

    public String getBegin() {
        return begin;
    }

    public String getEnd() {
        return end;
    }

    public boolean acceptOrder(String s) {
        if(begin != null && !"".equals(begin)) {
            if(s.compareTo(begin) > 0) {
                if(end != null && !"".equals(end)) {
                    return s.compareTo(end) < 0;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            if(end != null && !"".equals(end)) {
                return s.compareTo(end) < 0;
            } else {
                return true;
            }
        }
    }

    public boolean acceptFile(String filename) {
        return acceptFile(filename, this.feature);
    }

    public static boolean acceptFile(String filename, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(filename);
        return matcher.find();
    }

    public String getOrder(String name){
        return getOrder(name, regexp);
    }

    public static String getOrder(String name, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(name);
        return matcher.find() ? matcher.group() : "";
    }
}
