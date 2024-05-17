package com.erplus.sync.dao;



import com.erplus.sync.entity.es.ComponentEsEntity;
import com.erplus.sync.entity.es.GroupComponentEsEntity;
import com.erplus.sync.entity.template.TemplateComponent;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ComponentDao {


    Map<Integer, List<ComponentEsEntity>> selectOneCompanyAllComponent(Integer companyId, String createTime) throws SQLException;
    Map<Integer, List<GroupComponentEsEntity>> selectOneCompanyAllGroupComponent(Integer companyId, String createTime) throws SQLException;

    Map<Integer, List<ComponentEsEntity>> selectComponentByRequestIds(String requestIds) throws SQLException;

    Map<Integer, List<GroupComponentEsEntity>> selectGroupComponentByRequestIds(String requestIds) throws SQLException;

    List<ComponentEsEntity> selectComponentByRequestId(Integer requestId) throws SQLException;

    List<GroupComponentEsEntity> selectGroupComponentByRequestId(Integer requestId) throws SQLException;

    List<TemplateComponent> selectComponentByTemplateId(Integer templateId) throws SQLException;

    void updateNumTypeUniqueIdById(Integer num, Integer uniqueId, Integer type, Integer id) throws SQLException;

}
