package com.erplus.sync.dao;


import com.erplus.sync.entity.es.LeaveOvertimeOutdoorEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OutdoorDao {

    Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectOneCompanyAllOutdoor(Integer companyId, String createTime) throws SQLException;

    Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectOutdoorByRequestIds(String requestIds) throws SQLException;

    List<LeaveOvertimeOutdoorEsEntity> selectOutdoorByRequestId(Integer requestId) throws SQLException;

}
