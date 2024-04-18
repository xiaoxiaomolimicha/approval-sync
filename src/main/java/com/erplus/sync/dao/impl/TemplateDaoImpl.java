package com.erplus.sync.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.TemplateDao;
import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.entity.es.RequestEsEntity;
import com.erplus.sync.entity.template.MaxUniqueIdEntity;
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
            ps.executeUpdate();
        }
    }

    @Override
    public List<MaxUniqueIdEntity> selectOneTemplateAllComponents(Integer companyId) throws SQLException {
        List<MaxUniqueIdEntity> result = new ArrayList<>();
        String sql = "select Ftemplate_id, Fancestor_id, Ftemplate_component from request_template where Fcompany_id = ?";
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    MaxUniqueIdEntity maxUniqueIdEntity = new MaxUniqueIdEntity();
                    maxUniqueIdEntity.setTemplateId(rs.getInt(1));
                    maxUniqueIdEntity.setAncestorId(rs.getInt(2));
                    String templateComponents = rs.getString(3);
                    maxUniqueIdEntity.setTemplateComponents(templateComponents);
                    maxUniqueIdEntity.setTemplateComponentList(JSONObject.parseArray(templateComponents, TemplateComponent.class));
                    result.add(maxUniqueIdEntity);
                }
            }
        }
        return result;
    }
}
