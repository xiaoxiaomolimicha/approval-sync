package com.erplus.sync.dao.impl;

import com.erplus.sync.dao.ExpenseDao;
import com.erplus.sync.entity.es.ExpenseEsEntity;
import com.erplus.sync.utils.DateTimeHelper;
import com.erplus.sync.utils.SQLLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDaoImpl extends AbstractDao implements ExpenseDao {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseDaoImpl.class);

    private Connection connection;

    public ExpenseDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    String getQuerySql(String condition) {
        return null;
    }

    @Override
    Object getObject(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public Map<Integer, List<ExpenseEsEntity>> selectOneCompanyAllExpense(Integer companyId) throws SQLException{
        String sql = "select ex.request_id, ex.id, ex.pay_amount, ex.total_amount, ex.pay_date " +
                "from request_flow f " +
                "inner join sys_approval_expense ex " +
                "on f.Frequest_id = ex.request_id " +
                "where f.Ffinished != -99 and f.Fis_resubmit = 0 and f.Fcompany_id = ?";
        Map<Integer, List<ExpenseEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    ExpenseEsEntity expenseEsEntity = new ExpenseEsEntity();
                    expenseEsEntity.setRequest_id(rs.getInt(1));
                    expenseEsEntity.setId(rs.getInt(2));
                    expenseEsEntity.setPay_amount(rs.getBigDecimal(3).stripTrailingZeros().toPlainString());
                    expenseEsEntity.setTotal_amount(rs.getBigDecimal(4).stripTrailingZeros().floatValue());
                    expenseEsEntity.setPay_date(getTimeStr(rs.getTimestamp(5), DateTimeHelper.YEAR_MONTH_DAY_PATTERN));
                    map.putIfAbsent(expenseEsEntity.getRequest_id(), new ArrayList<>());
                    map.get(expenseEsEntity.getRequest_id()).add(expenseEsEntity);
                }
            }
        }
        return map;
    }
}
