package com.erplus.sync.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.entity.template.TemplateComponent;
import com.erplus.sync.utils.ListHelper;
import com.erplus.sync.utils.SQLLogger;
import com.erplus.sync.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemplateDaoImpl extends AbstractDao implements TemplateDao {

    private static final Logger logger = LoggerFactory.getLogger(TemplateDaoImpl.class);

    private Connection connection;

    public TemplateDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }


    @Override
    String getQuerySql(String condition) {
        return null;
    }

    @Override
    Object getObject(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public List<Integer> selectOneCompanyOldStopTemplate(Integer companyId) throws SQLException {
        List<Integer> templateIds = new ArrayList<>();
        String sql = "select max(Ftemplate_id) from request_template rt left join request_flow f on rt.Fancestor_id = f.Ftemplate_ancestor_id where Fancestor_id not in (select Fancestor_id from request_template where Fstatus = 1 and request_template.Fcompany_id = ?) and rt.Fcompany_id = ? group by rt.Fancestor_id having count(distinct f.Frequest_id) > 0 order by count(distinct f.Frequest_id) desc";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            ps.setInt(2, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    templateIds.add(rs.getInt(1));
                }
            }
        }
        return templateIds;
    }

    @Override
    public void updateNewStopStatusByTemplateId(List<Integer> templateIds, Integer companyId) throws SQLException {
        if (Utils.isEmpty(templateIds)) {
            logger.info("templateIds为空，不更新状态");
        }
        String sql = "update request_template set Fstatus = 2 where Fcompany_id = ? and Ftemplate_id in (" + ListHelper.list2string(templateIds) + ")";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            ps.executeUpdate();
        }
    }

    @Override
    public List<SimpleTemplate> selectOneCompanyAllTemplate(Integer companyId) throws SQLException {
        List<SimpleTemplate> result = new ArrayList<>();
        String sql = "select Ftemplate_id, Fancestor_id, Ftemplate_component, Fmax_unique_id, Fcompany_id from request_template where Fcompany_id = ?";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    SimpleTemplate simpleTemplate = getSimpleTemplate(rs);
                    result.add(simpleTemplate);
                }
            }
        }
        return result;
    }

    @Override
    public List<Integer> selectAllCompanyIdInTemplate() throws SQLException {
        List<Integer> companyIds = new ArrayList<>();
        String sql = "select Fcompany_id from request_template where Fcompany_id > 0 group by Fcompany_id";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            logger.info(sql);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    companyIds.add(rs.getInt(1));
                }
            }
        }
        return companyIds;
    }

    @Override
    public void updateMaxUniqueIdByAncestorId(Integer ancestorId, Integer maxUniqueId) throws SQLException {
        String sql = "update request_template set Fmax_unique_id = ? where Fancestor_id = ?";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, maxUniqueId);
            ps.setInt(2, ancestorId);
            logger.info(SQLLogger.logSQL(sql, maxUniqueId, ancestorId));
            ps.executeUpdate();
        }
    }

    @Override
    public List<SimpleTemplate> selectAllDefaultTemplate() throws SQLException {
        List<SimpleTemplate> simpleTemplates = new ArrayList<>();
        String sql = "select Ftemplate_id, Ftemplate_default_type, Ftemplate_component from request_template where Fcompany_id = 0";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            logger.info(SQLLogger.logSQL(sql));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    SimpleTemplate simpleTemplate = new SimpleTemplate();
                    simpleTemplate.setTemplateId(rs.getInt(1));
                    simpleTemplate.setDefaultType(rs.getInt(2));
                    String templateComponents = rs.getString(3);
                    simpleTemplate.setTemplateComponents(templateComponents);
                    simpleTemplates.add(simpleTemplate);
                }
            }
        }
        return simpleTemplates;
    }

    @Override
    public void updateDefaultTemplateComponents(Integer templateId, String templateComponents) throws SQLException {
        String sql = "update request_template set Ftemplate_component = ? where Ftemplate_id = ?";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setString(1, templateComponents);
            ps.setInt(2, templateId);
            logger.info(SQLLogger.logSQL(sql, templateComponents, templateId));
            ps.executeUpdate();
        }
    }

    @Override
    public List<SimpleTemplate> selectAllZeroMaxUniqueIdTemplate() throws SQLException {
        List<SimpleTemplate> result = new ArrayList<>();
        String sql = "select Ftemplate_id, Fancestor_id, Ftemplate_component, Fmax_unique_id, Fcompany_id from request_template where Fancestor_id in (select Fancestor_id from request_template where Fcompany_id > 0 and Fmax_unique_id = 0 group by Fancestor_id)";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            logger.info(SQLLogger.logSQL(sql));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    SimpleTemplate simpleTemplate = getSimpleTemplate(rs);
                    result.add(simpleTemplate);
                }
            }
        }
        return result;
    }

    private SimpleTemplate getSimpleTemplate(ResultSet rs) throws SQLException {
        SimpleTemplate simpleTemplate = new SimpleTemplate();
        simpleTemplate.setTemplateId(rs.getInt(1));
        simpleTemplate.setAncestorId(rs.getInt(2));
        String templateComponents = rs.getString(3);
        simpleTemplate.setTemplateComponents(templateComponents);
        simpleTemplate.setTemplateComponentList(JSONObject.parseArray(templateComponents, TemplateComponent.class));
        simpleTemplate.setMaxUniqueId((Integer) rs.getObject(4));
        simpleTemplate.setCompanyId(rs.getInt(5));
        return simpleTemplate;
    }

    @Override
    public List<SimpleTemplate> selectAllCompanyTemplate() throws SQLException {
        List<SimpleTemplate> result = new ArrayList<>();
        String sql = "select Ftemplate_id, Fancestor_id, Ftemplate_component, Fmax_unique_id, Fcompany_id from request_template where Ftemplate_id > 0";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            logger.info(sql);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    SimpleTemplate simpleTemplate = getSimpleTemplate(rs);
                    result.add(simpleTemplate);
                }
            }
        }
        return result;
    }

    @Override
    public SimpleTemplate selectSimpleTemplateById(Integer templateId) throws SQLException {
        String sql = "select Ftemplate_id, Fancestor_id, Ftemplate_component, Fmax_unique_id, Fcompany_id from request_template where Ftemplate_id = ?";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, templateId);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return getSimpleTemplate(rs);
                }
            }
        }
        return null;
    }

}
