package com.erplus.sync.dao;


import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.entity.es.RequestEsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.sql.SQLException;
import java.util.List;

public interface RequestDao {
    List<RequestEsEntity> selectOneCompanyAllEsRequest(Integer companyId, String creteTime) throws SQLException;

    List<RequestEsEntity> selectEsRequestByRequestIds(String requestIds) throws SQLException;

    RequestEsEntity selectEsRequestByRequestId(Integer requestId) throws SQLException;

    List<RequestFlow> selectOneCompanyAllRequest(Integer companyId) throws SQLException;

    List<Integer> selectAllCompanyIds() throws SQLException;
}
