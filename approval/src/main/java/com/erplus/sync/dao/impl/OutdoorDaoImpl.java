package com.erplus.sync.dao.impl;

import com.erplus.sync.dao.ExpenseDao;
import com.erplus.sync.dao.OutdoorDao;
import com.erplus.sync.entity.es.ExpenseEsEntity;
import com.erplus.sync.entity.es.LeaveOvertimeOutdoorEsEntity;
import com.erplus.sync.utils.SQLLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutdoorDaoImpl extends AbstractDao implements OutdoorDao {

    private static final Logger logger = LoggerFactory.getLogger(OutdoorDaoImpl.class);

    private Connection connection;

    public OutdoorDaoImpl(Connection connection) {
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
    public Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectOneCompanyAllOutdoor(Integer companyId, String createTime) throws SQLException {
        String sql = "select ao.request_id, ao.id, ao.duration " +
                "from request_flow f " +
                "inner join sys_approval_outdoor ao " +
                "on f.Frequest_id = ao.request_id " +
                "where f.Ffinished != -99 and f.Fis_resubmit = 0 and f.Fcompany_id = ?";
        if (StringUtils.isNotBlank(createTime)) {
            sql = sql + " and f.Fcreate_time >= '" + createTime + "'";
        }
        Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    LeaveOvertimeOutdoorEsEntity leaveOvertimeOutdoorEsEntity = new LeaveOvertimeOutdoorEsEntity();
                    leaveOvertimeOutdoorEsEntity.setRequest_id(rs.getInt(1));
                    leaveOvertimeOutdoorEsEntity.setId(rs.getInt(2));
                    leaveOvertimeOutdoorEsEntity.setDuration(rs.getInt(3));
                    map.putIfAbsent(leaveOvertimeOutdoorEsEntity.getRequest_id(), new ArrayList<>());
                    List<LeaveOvertimeOutdoorEsEntity> leaveOvertimeOutdoorEsEntities = map.get(leaveOvertimeOutdoorEsEntity.getRequest_id());
                    //只保存最新的一条数据
                    if (leaveOvertimeOutdoorEsEntities.isEmpty()) {
                        leaveOvertimeOutdoorEsEntities.add(leaveOvertimeOutdoorEsEntity);
                    } else {
                        LeaveOvertimeOutdoorEsEntity first = leaveOvertimeOutdoorEsEntities.get(0);
                        if (first.getId() < leaveOvertimeOutdoorEsEntity.getId()) {
                            leaveOvertimeOutdoorEsEntities.remove(0);
                            leaveOvertimeOutdoorEsEntities.add(leaveOvertimeOutdoorEsEntity);
                        }
                    }
                }
            }
        }
        return map;
    }
}
