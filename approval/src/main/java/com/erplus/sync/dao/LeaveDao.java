package com.erplus.sync.dao;


import com.erplus.sync.entity.es.LeaveOvertimeOutdoorEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface LeaveDao {

    Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectOneCompanyAllLeave(Integer companyId, String createTime) throws SQLException;

    Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectLeaveByRequestIds(String requestIds) throws SQLException;

    List<LeaveOvertimeOutdoorEsEntity> selectLeaveByRequestId(Integer requestId) throws SQLException;

}
