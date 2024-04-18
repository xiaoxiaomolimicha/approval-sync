package com.erplus.sync.dao;

import com.erplus.sync.entity.template.MaxUniqueIdEntity;

import java.sql.SQLException;
import java.util.List;

public interface TemplateDao {

    //查询一个公司所有旧停用模板
    List<Integer> selectOneCompanyOldStopTemplate(Integer companyId) throws SQLException;

    void updateNewStopStatusByTemplateId(List<Integer> templateIds, Integer companyId) throws SQLException;

    List<MaxUniqueIdEntity> selectOneCompanyAllTemplateComponents(Integer companyId) throws SQLException;

    List<Integer> selectAllCompanyIdInTemplate() throws SQLException;

    void updateMaxUniqueIdByAncestorId(Integer ancestorId, Integer maxUniqueId) throws SQLException;

}
