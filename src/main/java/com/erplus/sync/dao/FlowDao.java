package com.erplus.sync.dao;


import com.erplus.sync.entity.es.ApprovalFlowEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public interface FlowDao {
    Map<Integer, List<ApprovalFlowEsEntity>> selectOneCompanyAllFlow(Integer companyId) throws SQLException;
}
