package com.erplus.sync.dao.impl;


import com.erplus.sync.utils.DateTimeHelper;
import com.erplus.sync.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

abstract class AbstractDao {
    /**
     * 统一获取方案查询SQL
     *
     * @param condition 条件（where……[order by……]/[group by ……] [limit……]）
     * @return
     */
    abstract String getQuerySql(String condition);

    /**
     * 统一设置返回对象属性，跟getQuerySql对应
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    abstract Object getObject(ResultSet rs) throws SQLException;

    /**
     * 时间戳转化
     *
     * @param time
     * @return
     */
    String getTimeStr(Timestamp time) {
        try {
            return Utils.isNull(time) ? null : DateTimeHelper.format(time, DateTimeHelper.DEFAULT_PATTERN);
        } catch (Exception e) {
            return null;
        }
    }

    String getTimeStr(Timestamp time, String pattern) {
        try {
            return Utils.isNull(time) ? null : DateTimeHelper.format(time, pattern);
        } catch (Exception e) {
            return null;
        }
    }
}
