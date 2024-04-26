package com.erplus.sync.dao;

import com.erplus.sync.entity.template.SimpleTemplate;

import java.sql.SQLException;
import java.util.List;

public interface TemplateDao {

    //查询一个公司所有旧停用模板
    List<Integer> selectOneCompanyOldStopTemplate(Integer companyId) throws SQLException;

    void updateNewStopStatusByTemplateId(List<Integer> templateIds, Integer companyId) throws SQLException;

    List<SimpleTemplate> selectOneCompanyAllTemplate(Integer companyId) throws SQLException;

    List<Integer> selectAllCompanyIdInTemplate() throws SQLException;

    void updateMaxUniqueIdByAncestorId(Integer ancestorId, Integer maxUniqueId) throws SQLException;

    List<SimpleTemplate> selectAllDefaultTemplate() throws SQLException;

    void updateDefaultTemplateComponents(Integer templateId, String templateComponents) throws SQLException;

    List<SimpleTemplate> selectAllZeroMaxUniqueIdTemplate() throws SQLException;

    List<SimpleTemplate> selectAllCompanyTemplate() throws SQLException;

    SimpleTemplate selectSimpleTemplateById(Integer templateId) throws SQLException;

}
