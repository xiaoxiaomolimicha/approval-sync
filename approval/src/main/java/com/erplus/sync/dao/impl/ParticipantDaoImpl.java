package com.erplus.sync.dao.impl;

import com.erplus.sync.dao.ParticipantDao;
import com.erplus.sync.entity.es.ParticipantEsEntity;
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

public class ParticipantDaoImpl extends AbstractDao implements ParticipantDao {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantDaoImpl.class);

    private Connection connection;

    public ParticipantDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public Map<Integer, List<ParticipantEsEntity>> selectOneCompanyAllParticipant(Integer companyId, String createTime) throws SQLException {
        String sql = "select id, request_id, contact_id, company_info_id, create_time " +
                "from request_flow rf " +
                "inner join sys_approval_participant p " +
                "on rf.Frequest_id = p.request_id " +
                "where rf.Fcompany_id = ? and p.state = 1 and rf.Ffinished != -99 and rf.Fis_resubmit = 0";
        if (StringUtils.isNotBlank(createTime)) {
            sql = sql + " and rf.Fcreate_time >= '" + createTime + "'";
        }
        Map<Integer, List<ParticipantEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    ParticipantEsEntity participantEs = new ParticipantEsEntity();
                    participantEs.setId(rs.getInt(1));
                    participantEs.setRequest_id(rs.getInt(2));
                    participantEs.setContact_id(rs.getInt(3));
                    participantEs.setCompany_info_id(rs.getInt(4));
                    participantEs.setCreate_time(getTimeStr(rs.getTimestamp(5)));

                    map.putIfAbsent(participantEs.getRequest_id(), new ArrayList<>());
                    map.get(participantEs.getRequest_id()).add(participantEs);
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
