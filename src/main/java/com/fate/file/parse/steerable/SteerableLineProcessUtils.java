package com.fate.file.parse.steerable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ydc on 2019/12/17.
 */
public final class SteerableLineProcessUtils {

    public static List<FieldSpecification> split2List(String line, String splitType, List<FieldSpecification> specifications){
        switch (splitType){
            case "FSL":
            case "FS":
            case "S":
            case "FSE":
            case "SE":
                return splitBySpecification(line, splitType, specifications);
            default:
                return splitBySpacer(line, splitType, specifications);

        }
    }

    public static Map<String, FieldSpecification> split2Map(String line, String splitType, List<FieldSpecification> specifications){
        switch (splitType){
            case "FSL":
            case "FS":
            case "S":
            case "FSE":
            case "SE":
                return splitBySpecificationMap(line, splitType, specifications);
            default:
                return splitBySpacerMap(line, splitType, specifications);
        }
    }

    public static List<FieldSpecification> splitBySpacer(String line, String spacer, List<FieldSpecification> specifications) {
        String[] fields = line.split(spacer);
        List<FieldSpecification> row = new LinkedList<>();
        for (FieldSpecification specification : specifications) {
            FieldSpecification clone = specification.clone();
            clone.setValByPos(fields);
            row.add(clone);
        }
        return row;
    }

    public static Map<String, FieldSpecification> splitBySpacerMap(String line, String spacer, List<FieldSpecification> specifications) {
        String[] fields = line.split(spacer);
        Map<String, FieldSpecification> row = new LinkedHashMap<>();
        for (FieldSpecification specification : specifications) {
            FieldSpecification clone = specification.clone();
            clone.setValByPos(fields);
            row.put(clone.getCol(), clone);
        }
        return row;
    }

    /**
     * provide method of line parser by specification
     *
     * @param line
     * @param splitType
     * @param specifications
     * @return
     */
    public static List<FieldSpecification> splitBySpecification(String line, String splitType, List<FieldSpecification> specifications) {
        List<FieldSpecification> list = new LinkedList<>();
        for (FieldSpecification field : specifications) {
            FieldSpecification clone = fillField(field, line, splitType);
            list.add(clone);
        }
        return list;
    }

    public static Map<String, FieldSpecification> splitBySpecificationMap(String line, String splitType, List<FieldSpecification> specifications) {
        Map<String, FieldSpecification> map = new LinkedHashMap<>();
        for (FieldSpecification field : specifications) {
            FieldSpecification clone = fillField(field, line, splitType);
            map.put(clone.getCol(), clone);
        }
        return map;
    }

    private static FieldSpecification fillField(FieldSpecification field, String line, String splitType){
        FieldSpecification clone = field.clone();
        int start = clone.getBegin();
        int end = clone.getEnd();
        int length = clone.getLen();
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
        clone.setVal(val);
        return clone;
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
