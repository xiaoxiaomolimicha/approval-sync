package com.erplus.sync.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ListHelper {

    private static final Logger logger = LoggerFactory.getLogger(ListHelper.class);

    public static <T> List<List<T>> divideList(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();
        if (Utils.isEmpty(list)) {
            return result;
        }
        for (int start = 0; start < list.size(); start += size) {
            int end = Math.min(start + size, list.size());
            result.add(list.subList(start, end));
        }
        return result;
    }

    public static <T> String list2string(List<T> list) {
        String str = list.toString().substring(1, list.toString().length() - 1);
        return str.replaceAll(" ", "");
    }
}
