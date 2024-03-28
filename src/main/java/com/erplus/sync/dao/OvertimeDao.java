package com.erplus.sync.dao;


import com.erplus.sync.entity.es.LeaveOvertimeOutdoorEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OvertimeDao {

    Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectOneCompanyOvertime(Integer companyId ) throws SQLException;

}
