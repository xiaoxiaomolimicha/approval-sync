package com.erplus.sync.dao;


import com.erplus.sync.entity.es.ApprovalFlowEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public interface FlowDao {
    Map<Integer, List<ApprovalFlowEsEntity>> selectOneCompanyAllFlow(Integer companyId, String createTime) throws SQLException;

    Map<Integer, List<ApprovalFlowEsEntity>> selectFlowByRequestIds(String requestIds) throws SQLException;

    List<ApprovalFlowEsEntity> selectFlowByRequestId(Integer requestId) throws SQLException;

}
