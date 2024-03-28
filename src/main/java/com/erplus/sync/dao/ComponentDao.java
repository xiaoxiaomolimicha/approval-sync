package com.erplus.sync.dao;



import com.erplus.sync.entity.es.ComponentEsEntity;
import com.erplus.sync.entity.es.GroupComponentEsEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ComponentDao {


    Map<Integer, List<ComponentEsEntity>> selectOneCompanyAllComponent (Integer companyId) throws SQLException;
    Map<Integer, List<GroupComponentEsEntity>> selectOneCompanyAllGroupComponent(Integer companyId) throws SQLException;

}
