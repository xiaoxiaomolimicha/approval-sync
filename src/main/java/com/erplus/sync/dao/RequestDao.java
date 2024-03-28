package com.erplus.sync.dao;


import com.erplus.sync.entity.es.RequestEsEntity;

import java.sql.SQLException;
import java.util.List;

public interface RequestDao {
    List<RequestEsEntity> selectOneCompanyAllEsRequest(Integer companyId) throws SQLException;
}
