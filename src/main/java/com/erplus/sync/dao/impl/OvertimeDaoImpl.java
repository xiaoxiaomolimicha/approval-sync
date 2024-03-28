package com.erplus.sync.dao.impl;

import com.erplus.sync.dao.OvertimeDao;
import com.erplus.sync.entity.es.LeaveOvertimeOutdoorEsEntity;
import com.erplus.sync.utils.SQLLogger;
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

public class OvertimeDaoImpl extends AbstractDao implements OvertimeDao {

    private static final Logger logger = LoggerFactory.getLogger(OvertimeDaoImpl.class);

    private Connection connection;

    public OvertimeDaoImpl(Connection connection) {
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
    public Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> selectOneCompanyOvertime(Integer companyId) throws SQLException {
        String sql = "select ao.request_id, ao.id, ao.duration " +
                "from request_flow f " +
                "inner join sys_approval_overtime ao " +
                "on f.Frequest_id = ao.request_id " +
                "where f.Ffinished != -99 and f.Fis_resubmit = 0 and f.Fcompany_id = ?";
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
                    map.get(leaveOvertimeOutdoorEsEntity.getRequest_id()).add(leaveOvertimeOutdoorEsEntity);
                }
            }
        }
        return map;
    }
}
