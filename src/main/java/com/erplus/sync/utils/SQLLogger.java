package com.erplus.sync.utils;

public class SQLLogger {
    public static String logSQL(String sql, Object... params) {
        int i = 0;
        String result = sql;

        try {
            while (result.contains("?")) {
                String param = params[i] == null ? "[paramsNull]" : String
                        .valueOf(params[i]);
                if (params[i] instanceof String) {
                    param = "'" + param + "'";
                }
                result = result.replaceFirst("\\?", param);
                i++;
            }
        }catch (Exception e)
        {
            return e.getMessage();
        }
        return result;
    }
}
