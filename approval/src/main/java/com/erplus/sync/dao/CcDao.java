package com.erplus.sync.dao;

import com.erplus.sync.entity.RequestFiled;
import com.erplus.sync.entity.es.RequestFieldEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 抄送记录表
 */
public interface CcDao {

    Map<Integer, List<RequestFieldEsEntity>> selectOneCompanyAllCc(Integer companyId, String createTime) throws SQLException;

    Map<Integer, List<RequestFieldEsEntity>> selectCCByRequestIds(String requestIds) throws SQLException;

    List<RequestFieldEsEntity> selectCCByRequestId(Integer requestId) throws SQLException;

    List<RequestFiled> selectOneCompanyAllRequestFiled(Integer companyId) throws SQLException;
}
