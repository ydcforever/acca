package com.fate.file.parse.steerable;

import java.util.*;

/**
 * Created by ydc on 2019/12/17.
 */
public final class SteerableLineProcessUtils {

    /**
     * 填充行
     * @param line
     * @param splitType
     * @param specifications
     */
    public static void fillRow(String line, String splitType, Map<String, FieldSpecification> specifications){
        switch (splitType){
            case "FSL":
            case "FS":
            case "S":
            case "FSE":
            case "SE":
                splitBySpecification(line, splitType, specifications);
            default:
                splitBySpacer(line, splitType, specifications);
        }
    }

    /**
     * 按分割符填充
     * @param line
     * @param spacer
     * @param specifications
     */
    private static void splitBySpacer(String line, String spacer, Map<String, FieldSpecification> specifications) {
        String[] fields = line.split(spacer);
        Collection<FieldSpecification> collection = specifications.values();
        for (FieldSpecification field : collection) {
            if(!field.isDefine()) {
                field.clear();
                field.setValByPos(fields);
            }
        }
    }

    /**
     * 按位截取填充
     *
     * @param line
     * @param splitType
     * @param specifications
     * @return
     */
    private static void splitBySpecification(String line, String splitType, Map<String, FieldSpecification> specifications) {
        Collection<FieldSpecification> collection = specifications.values();
        for (FieldSpecification field : collection) {
            if(!field.isDefine()){
                fillField(field, line, splitType);
            }
        }
    }

    private static void fillField(FieldSpecification field, String line, String splitType){
        field.clear();
        int start = field.getBegin();
        int end = field.getEnd();
        int length = field.getLen();
        String val = "";
        if (SplitType.S.compareTo(splitType)) {
            val = line.substring(start).trim();
        } else if (SplitType.SE.compareTo(splitType)) {
            val = fixSubstring(line, start, end);
        } else if (SplitType.FSE.compareTo(splitType)) {
            val = fixSubstring(line, start - 1, end);
        } else if (SplitType.SL.compareTo(splitType)) {
            val = fixSubstring(line, start, start + length);
        } else if (SplitType.FSL.compareTo(splitType)) {
            val = fixSubstring(line, start - 1, start - 1 + length);
        }
        field.setVal(val);
    }

    public static String fixSubstring(String line, int start, int end) {
        int len = line.length();
        if (end >= len) {
            return line.substring(start).trim();
        } else {
            return line.substring(start, end).trim();
        }
    }
}
