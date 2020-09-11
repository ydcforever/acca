package com.btw.parser.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonUtil {
    public static String object2json(Object obj) {
        StringBuilder json = new StringBuilder();
        if (obj == null) {
            json.append("\"\"");
        } else if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Boolean || obj instanceof Short || obj instanceof Double || obj instanceof Long || obj instanceof BigDecimal || obj instanceof BigInteger || obj instanceof Byte) {
            json.append("\"").append(string2json(obj.toString())).append("\"");
        } else if (obj instanceof Object[]) {
            json.append(array2json((Object[]) obj));
        } else if (obj instanceof List) {
            json.append(list2json((List<?>) obj));
        } else if (obj instanceof Map) {
            json.append(map2json((Map<?, ?>) obj));
        } else if (obj instanceof Set) {
            json.append(set2json((Set<?>) obj));
        } else if (obj instanceof Date) {
            json.append("\"").append(string2json(new SimpleDateFormat("yyyy-MM-dd").format(obj))).append("\"");
        } else if (obj instanceof byte[]) {
            try {
                json.append("\"").append(string2json(new String((byte[]) obj, "utf-8"))).append("\"");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            json.append(bean2json(obj));
        }
        return json.toString();
    }

    public static String bean2json(Object bean) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        PropertyDescriptor[] props = null;
        try {
            props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
        } catch (IntrospectionException e) {
        }
        if (props != null) {
            for (int i = 0; i < props.length; i++) {
                try {
                    String name = object2json(props[i].getName());
                    String value = object2json(props[i].getReadMethod().invoke(bean));
                    json.append(name);
                    json.append(":");
                    json.append(value);
                    json.append(",");
                } catch (Exception e) {
                }
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    public static String list2json(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String array2json(Object[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (array != null && array.length > 0) {
            for (Object obj : array) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String map2json(Map<?, ?> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (map != null && map.size() > 0) {
            for (Object key : map.keySet()) {
                json.append(object2json(key));
                json.append(":");
                json.append(object2json(map.get(key)));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    public static String set2json(Set<?> set) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (set != null && set.size() > 0) {
            for (Object obj : set) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String string2json(String s) {
        if (s == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if (ch <= '\u001F') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * @param str
     * @param array
     * @return 单一值数组
     */
    public static List<String> getList(String str, List<String> array) {
        if (!str.contains(",")) {
            array.add(str.substring(str.indexOf("\"") + 1, str.lastIndexOf("]") - 1));
            return array;
        } else {
            String temp1 = str.substring(0, str.indexOf(","));
            String temp2 = str.substring(str.indexOf(",") + 1);
            array.add(temp1.substring(temp1.indexOf("[") + 2, temp1.lastIndexOf("\"")));
            return getList(temp2, array);
        }
    }

    /**
     * @param str
     * @param map
     * @return bean属性集合
     */
    public static HashMap<String, Object> getBean(String str, HashMap<String, Object> map) {
        if (!str.contains(",")) {
            String key0 = str.substring(str.indexOf("\"") + 1, str.indexOf(":") - 1);
            String value0 = str.substring(str.indexOf(":") + 2, str.lastIndexOf("\""));
            map.put(key0, value0);
            return map;
        } else {
            String temp1 = str.substring(str.indexOf("\""), str.indexOf(","));
            String temp2 = str.substring(str.indexOf(",") + 1);
            String key = temp1.substring(temp1.indexOf("\"") + 1, temp1.indexOf(":") - 1);
            String value = temp1.substring(temp1.indexOf(":") + 2, temp1.lastIndexOf("\""));
            map.put(key, value);
            getBean(temp2, map);
        }
        return map;
    }

    /**
     * @param str
     * @param list
     * @return bean对象集合
     */
    public static List<HashMap<String, Object>> getBeanList(String str, List<HashMap<String, Object>> list) {
        if (!str.contains("}"))
            return list;
        else {
            String temp1 = str.substring(str.indexOf("{") + 1, str.indexOf("}"));
            String temp2 = str.substring(str.indexOf("}") + 2);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map = JsonUtil.getBean(temp1, map);
            list.add(map);
            return getBeanList(temp2, list);
        }

    }
}