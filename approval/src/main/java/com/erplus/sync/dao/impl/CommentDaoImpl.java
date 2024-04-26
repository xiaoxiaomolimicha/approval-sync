package com.erplus.sync.dao.impl;

import com.erplus.sync.entity.es.CommentEsEntity;
import com.erplus.sync.utils.SQLLogger;
import com.erplus.sync.dao.CommentDao;
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

public class CommentDaoImpl extends AbstractDao implements CommentDao {

    private static final Logger logger = LoggerFactory.getLogger(CommentDaoImpl.class);

    private Connection connection;

    public CommentDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }



    @Override
    public Map<Integer, List<CommentEsEntity>> selectOneCompanyAllComment(Integer companyId, String createTime) throws SQLException {
        String sql = "select rc.Fid, rc.Frequest_id, rc.Fcrt_at, rc.Fmessage " +
                "from request_flow f " +
                "inner join request_comment rc " +
                "on f.Frequest_id = rc.Frequest_id " +
                "where rc.Fcompany_id = ? and rc.Fdelete_at is not null and rc.Fis_cancel = 0 and f.Ffinished != -99 and f.Fis_resubmit = 0";
        if (StringUtils.isNotBlank(createTime)) {
            sql = sql + " and f.Fcreate_time >= '" + createTime + "'";
        }
        Map<Integer, List<CommentEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    String message = rs.getString(4);
                    //空数据不同步
                    if (StringUtils.isBlank(message)) {
                        continue;
                    }
                    CommentEsEntity commentEs = new CommentEsEntity();
                    commentEs.setId(rs.getInt(1));
                    commentEs.setRequest_id(rs.getInt(2));
                    commentEs.setCrt_at(getTimeStr(rs.getTimestamp(3)));
                    commentEs.setMessage(message);
                    map.putIfAbsent(commentEs.getRequest_id(), new ArrayList<>());
                    map.get(commentEs.getRequest_id()).add(commentEs);
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
