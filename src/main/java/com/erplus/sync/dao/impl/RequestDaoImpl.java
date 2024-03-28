package com.erplus.sync.dao.impl;

import com.erplus.sync.entity.es.RequestEsEntity;
import com.erplus.sync.utils.SQLLogger;
import com.erplus.sync.dao.RequestDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RequestDaoImpl extends AbstractDao implements RequestDao {

    private static final Logger logger = LoggerFactory.getLogger(RequestDaoImpl.class);

    private Connection connection;

    public RequestDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }



    @Override
    public List<RequestEsEntity> selectOneCompanyAllEsRequest(Integer companyId) throws SQLException {
        String sql = "select Frequest_id, Fcompany_id, Fapproval_num, Frequest_name, Fapplicant_ciid, " +
                "Fproxy_contact_id, Ffinished, Ffinancial_status, Finvoice_status, Ftemplate_ancestor_id, " +
                "Frequest_templet, Fcreate_time, Ffinally_confirmed_time, Ffinally_cc_time, Fapplicant, " +
                "Frequest_template_type " +
                "from request_flow where Fcompany_id = ? and Ffinished != -99 and Fis_resubmit = 0";
        List<RequestEsEntity> requests = new ArrayList<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql, companyId));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    RequestEsEntity requestEs = new RequestEsEntity();
                    requestEs.setRequest_id(rs.getInt(1));
                    requestEs.setCompany_id(rs.getInt(2));
                    requestEs.setApproval_num(rs.getString(3));
                    requestEs.setRequest_name(rs.getString(4));
                    requestEs.setApplicant_ciid(rs.getInt(5));

                    requestEs.setProxy_contact_id(rs.getInt(6));
                    requestEs.setFinished(rs.getInt(7));
                    requestEs.setFinancial_status(rs.getInt(8));
                    requestEs.setInvoice_status(rs.getInt(9));
                    requestEs.setTemplate_ancestor_id(rs.getInt(10));

                    requestEs.setTemplate_id(rs.getInt(11));
                    requestEs.setCreate_time(getTimeStr(rs.getTimestamp(12)));
                    requestEs.setFinally_confirmed_time(getTimeStr(rs.getTimestamp(13)));
                    requestEs.setFinally_cc_time(getTimeStr(rs.getTimestamp(14)));
                    requestEs.setApplicant(rs.getInt(15));

                    requestEs.setDefault_type(rs.getInt(16));
                    requests.add(requestEs);
                }
            }
        }
        return requests;
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
