package com.erplus.sync.dao;


import com.erplus.sync.entity.es.LeaveOvertimeOutdoorEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OutdoorDao {

    Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectOneCompanyAllOutdoor(Integer companyId ) throws SQLException;

}
