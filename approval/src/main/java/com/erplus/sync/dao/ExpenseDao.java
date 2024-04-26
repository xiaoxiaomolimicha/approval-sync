package com.erplus.sync.dao;


import com.erplus.sync.entity.es.ExpenseEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ExpenseDao {

    Map<Integer, List<ExpenseEsEntity>> selectOneCompanyAllExpense(Integer companyId, String createTime) throws SQLException;

}
