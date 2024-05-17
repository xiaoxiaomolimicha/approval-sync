package com.erplus.sync.dao.impl;

import com.erplus.sync.entity.RequestFiled;
import com.erplus.sync.entity.es.RequestFieldEsEntity;
import com.erplus.sync.utils.SQLLogger;
import com.erplus.sync.dao.CcDao;
import org.apache.commons.lang3.StringUtils;
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
    public Map<Integer, List<RequestFieldEsEntity>> selectOneCompanyAllCc(Integer companyId, String createTime) throws SQLException {
        String sql = "select rf.Fid, rf.Frequest_id, rf.Fwho_filed_ciid, rf.Ffiled_status, rf.Fcreate_time " +
                "from request_flow f " +
                "inner join request_filed rf " +
                "on f.Frequest_id = rf.Frequest_id " +
                "where f.Fcompany_id = ? and f.Ffinished != -99 and rf.Fstatus = 0 and f.Fis_resubmit = 0";
        if (StringUtils.isNotBlank(createTime)) {
            sql = sql + " and f.Fcreate_time >= '" + createTime + "'";
        }
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

    @Override
    public Map<Integer, List<RequestFieldEsEntity>> selectCCByRequestIds(String requestIds) throws SQLException {
        String sql = "select Fid, Frequest_id, Fwho_filed_ciid, Ffiled_status, Fcreate_time from request_filed where Frequest_id in (" + requestIds + ")";
        Map<Integer, List<RequestFieldEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPrepareStatement(sql)){
            logger.info(sql);
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

    public List<RequestFieldEsEntity> selectCCByRequestId(Integer requestId) throws SQLException {
        String sql = "select Fid, Fwho_filed_ciid, Ffiled_status, Fcreate_time from request_filed where Frequest_id = ?";
        List<RequestFieldEsEntity> list = new ArrayList<>();
        try (PreparedStatement ps = getPrepareStatement(sql)){
            ps.setInt(1, requestId);
            logger.info(SQLLogger.logSQL(sql, requestId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    RequestFieldEsEntity requestFieldEs = new RequestFieldEsEntity();
                    requestFieldEs.setId(rs.getInt(1));
                    requestFieldEs.setWho_filed_ciid(rs.getInt(2));
                    requestFieldEs.setFiled_status(rs.getInt(3));
                    requestFieldEs.setCreate_time(getTimeStr(rs.getTimestamp(4)));
                    list.add(requestFieldEs);
                }
            }
        }
        return list;
    }

    @Override
    public List<RequestFiled> selectOneCompanyAllRequestFiled(Integer companyId) throws SQLException {
        String sql = "select Fid, Frequest_id, Fwho_filed, Fwho_filed_mcid, Fwho_filed_ciid, " +
                "Ffiled_time, Fcompany_id, Fstatus, Fdisable_time, Fcreate_time, " +
                "Ftemplate_type, Fis_temp, Fcrt_by, Ffiled_status, Fis_read " +
                "from request_filed where Fcompany_id = ?";
        List<RequestFiled> list = new ArrayList<>();
        try (PreparedStatement ps = getPrepareStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    try {
                        RequestFiled requestFiled = new RequestFiled();
                        requestFiled.setId(rs.getInt(1));
                        requestFiled.setRequestId(rs.getInt(2));
                        requestFiled.setWhoFiled(rs.getInt(3));
                        requestFiled.setWhoFiledMcid(rs.getInt(4));
                        requestFiled.setWhoFiledCiid(rs.getInt(5));

                        requestFiled.setFiledTime(getTimeStr(rs.getTimestamp(6)));
                        requestFiled.setCompanyId(rs.getInt(7));
                        requestFiled.setStatus(rs.getInt(8));
                        requestFiled.setDisableTime(rs.getTimestamp(9));
                        requestFiled.setDisableTimeStr(getTimeStr(rs.getTimestamp(9)));
                        requestFiled.setCreateTime(rs.getTimestamp(10));
                        requestFiled.setCreateTimeStr(getTimeStr(rs.getTimestamp(10)));

                        requestFiled.setTemplateType(rs.getInt(11));
                        requestFiled.setIsTemp(rs.getInt(12));
                        requestFiled.setCrtBy(rs.getInt(13));
                        requestFiled.setFiledStatus(rs.getInt(14));
                        requestFiled.setIsRead(rs.getInt(15));


                        list.add(requestFiled);

                    } catch (Exception e) {
                        logger.error("有问题的 requestFiled: {}", rs.getInt(1));
                        throw e;
                    }
                }
            }
        }
        return list;
    }
}