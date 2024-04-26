package com.erplus.sync.utils;

import java.util.Collection;
import java.util.Map;

public class Utils {
    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Collection c) {
        return !isEmpty(c);
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isNullArray(Object[] objs) {
        return objs == null || objs.length == 0;
    }

    public static boolean isNotNullArray(Object[] objs) {
        return !isNullArray(objs);
    }
}
