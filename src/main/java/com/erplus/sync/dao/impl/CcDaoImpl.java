package com.erplus.sync.dao.impl;

import com.erplus.sync.entity.es.RequestFieldEsEntity;
import com.erplus.sync.utils.SQLLogger;
import com.erplus.sync.dao.CcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by liuqi on 2018/7/3.
 */
public class CcDaoImpl extends AbstractDao implements CcDao {

    private static final Logger logger = LoggerFactory.getLogger(CcDaoImpl.class);

    private Connection conn;

    public CcDaoImpl(Connection conn) {
        this.conn = conn;
    }

    private PreparedStatement getPrepareStatement(String sql) throws SQLException {
        return conn.prepareStatement(sql);
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
    public Map<Integer, List<RequestFieldEsEntity>> selectOneCompanyAllCc(Integer companyId) throws SQLException {
        String sql = "select rf.Fid, rf.Frequest_id, rf.Fwho_filed_ciid, rf.Ffiled_status, rf.Fcreate_time " +
                "from request_flow f " +
                "inner join request_filed rf " +
                "on f.Frequest_id = rf.Frequest_id " +
                "where f.Fcompany_id = ? and f.Ffinished != -99 and rf.Fstatus = 0 and f.Fis_resubmit = 0";
        Map<Integer, List<RequestFieldEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPrepareStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    RequestFieldEsEntity requestFieldEs= new RequestFieldEsEntity();
                    requestFieldEs.setId(rs.getInt(1));
                    requestFieldEs.setRequest_id(rs.getInt(2));
                    requestFieldEs.setWho_filed_ciid(rs.getInt(3));
                    requestFieldEs.setFiled_status(rs.getInt(4));
                    requestFieldEs.setCreate_time(getTimeStr(rs.getTimestamp(5)));

                    map.putIfAbsent(requestFieldEs.getRequest_id(), new ArrayList<>());
                    map.get(requestFieldEs.getRequest_id()).add(requestFieldEs);
                }
            }
        }
        return map;
    }
}