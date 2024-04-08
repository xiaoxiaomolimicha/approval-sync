package com.erplus.sync.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.entity.Constants;
import com.erplus.sync.dao.ComponentDao;
import com.erplus.sync.entity.es.ComponentEsEntity;
import com.erplus.sync.entity.es.GroupComponentEsEntity;
import com.erplus.sync.entity.es.GroupNestedComponent;
import com.erplus.sync.utils.ComponentUtils;
import com.erplus.sync.utils.SQLLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ComponentImpl extends AbstractDao implements ComponentDao {

    private static final Logger logger = LoggerFactory.getLogger(ComponentImpl.class);

    private Connection connection;

    public ComponentImpl(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public Map<Integer, List<ComponentEsEntity>> selectOneCompanyAllComponent(Integer companyId) throws SQLException {
        String sql = "select rc.Frequest_id, rc.Fid, rc.Fcomponent_num, rc.Funique_id, rc.Fcontent, rc.Fcreate_time, rc.Fcontent_type " +
                "from request_flow rf " +
                "inner join request_content rc " +
                "on rf.Frequest_id = rc.Frequest_id " +
                "where rf.Fcompany_id = ? and rf.Ffinished != -99 and rf.Fis_resubmit = 0";
        Map<Integer, List<ComponentEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    int type = rs.getInt(7);
                    int num = rs.getInt(3);
                    String value = rs.getString(5);
                    //不需要上传到es的组件
                    if (Constants.noNeedSyncTypes.contains(type)
                            || (Objects.equals(type, 0) && Objects.equals(num, 0)) //被删除了数据
                            || !ComponentUtils.needSyncComponentToEs(value, type)) { //空数据
                        continue;
                    }
                    ComponentEsEntity componentEs = new ComponentEsEntity();
                    componentEs.setRequest_id(rs.getInt(1));
                    componentEs.setId(rs.getInt(2));
                    componentEs.setNum(num);
                    componentEs.setUnique_id(rs.getInt(4));
                    String formatValue = ComponentUtils.formatEsComponentValue(value, type);
                    String dateValue = ComponentUtils.getDateValue(value, type);
                    Float floatValue = ComponentUtils.getFloatValue(value, type);
                    componentEs.setValue(formatValue);
                    componentEs.setDate_value(dateValue);
                    componentEs.setFloat_value(floatValue);
                    componentEs.setCreate_time(getTimeStr(rs.getTimestamp(6)));
                    componentEs.setType(type);
                    map.putIfAbsent(componentEs.getRequest_id(), new ArrayList<>());
                    map.get(componentEs.getRequest_id()).add(componentEs);
                }
            }
        }
        return map;
    }

    @Override
    public Map<Integer, List<GroupComponentEsEntity>> selectOneCompanyAllGroupComponent(Integer companyId) throws SQLException {
        String sql = "select acg.id, acg.request_id, acg.component_group_id, create_time, acg.value " +
                "from request_flow rf " +
                "inner join sys_approval_component_group_value acg " +
                "on rf.Frequest_id = acg.request_id " +
                "where rf.Fcompany_id = ? and rf.Ffinished != -99 and Fis_resubmit = 0";
        Map<Integer, List<GroupComponentEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    GroupComponentEsEntity groupComponentEs = new GroupComponentEsEntity();
                    groupComponentEs.setId(rs.getInt(1));
                    groupComponentEs.setRequest_id(rs.getInt(2));
                    groupComponentEs.setComponent_group_id(rs.getInt(3));
                    groupComponentEs.setCreate_time(getTimeStr(rs.getTimestamp(4)));
                    String value = rs.getString(5);
                    if (StringUtils.isBlank(value)) {
                        groupComponentEs.setComponent(new ArrayList<>());
                    } else {
                        List<GroupNestedComponent> groupNestedComponents = JSONObject.parseArray(value, GroupNestedComponent.class);
                        List<GroupNestedComponent> needSyncGroupNestedComponents = new ArrayList<>();
                        for (GroupNestedComponent groupNestedComponent : groupNestedComponents) {
                            String componentValue = groupNestedComponent.getValue();
                            Integer type = groupNestedComponent.getType();
                            if (Constants.noNeedSyncTypes.contains(type)
                                    || !ComponentUtils.needSyncComponentToEs(componentValue, type)){
                                continue;
                            }
                            String formatValue = ComponentUtils.formatEsComponentValue(componentValue, type);
                            String dateValue = ComponentUtils.getDateValue(componentValue, type);
                            Float floatValue = ComponentUtils.getFloatValue(componentValue, type);
                            groupNestedComponent.setValue(formatValue);
                            if (dateValue != null) {
                                groupNestedComponent.setDate_value(dateValue);
                            }
                            if (floatValue != null) {
                                groupNestedComponent.setFloat_value(floatValue);
                            }
                            needSyncGroupNestedComponents.add(groupNestedComponent);
                        }
                        groupComponentEs.setComponent(needSyncGroupNestedComponents);
                    }
                    map.putIfAbsent(groupComponentEs.getRequest_id(), new ArrayList<>());
                    map.get(groupComponentEs.getRequest_id()).add(groupComponentEs);

                }
            }
        }
        return map;
    }


    @Override
    String getQuerySql(String condition) {
        return null;
    }

    @Override
    Object getObject(ResultSet rs) throws SQLException {
        return null;
    }
}
