package com.erplus.sync.dao.impl;

import com.erplus.sync.dao.FlowDao;
import com.erplus.sync.entity.es.ApprovalFlowEsEntity;
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

public class FlowDaoImpl extends AbstractDao implements FlowDao {

    private static final Logger logger = LoggerFactory.getLogger(FlowDaoImpl.class);

    private Connection connection;

    public FlowDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public Map<Integer, List<ApprovalFlowEsEntity>> selectOneCompanyAllFlow(Integer companyId) throws SQLException {
        String sql = "select id, request_id, state, is_approved, is_approved_at, " +
                "company_info_id, contact_id " +
                "from request_flow f " +
                "inner join sys_approval_flow flow " +
                "on f.Frequest_id = flow.request_id " +
                "where f.Fcompany_id = ? and f.Ffinished != -99 and flow.state != 3 and f.Fis_resubmit = 0";
        Map<Integer, List<ApprovalFlowEsEntity>> map = new HashMap<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    ApprovalFlowEsEntity flowEs = new ApprovalFlowEsEntity();
                    flowEs.setId(rs.getInt(1));
                    flowEs.setRequest_id(rs.getInt(2));
                    flowEs.setState(rs.getInt(3));
                    flowEs.setIs_approved(rs.getInt(4));
                    flowEs.setIs_approved_at(getTimeStr(rs.getTimestamp(5)));

                    flowEs.setCompany_info_id(rs.getInt(6));
                    flowEs.setContact_id(rs.getInt(7));
                    map.putIfAbsent(flowEs.getRequest_id(), new ArrayList<>());
                    map.get(flowEs.getRequest_id()).add(flowEs);
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
