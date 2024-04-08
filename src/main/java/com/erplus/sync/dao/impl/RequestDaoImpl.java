package com.erplus.sync.dao.impl;

import com.erplus.sync.entity.RequestFlow;
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
    public List<RequestFlow> selectOneCompanyAllRequest(Integer companyId) throws SQLException {
        String sql = "select Frequest_id, Frequest_name, Frequest_templet, Frequest_step, Fcompany_id, " +
                "Fapplicant, Fapplicant_ciid, Fcreate_time, Fchecked_time, Fcall_back_time, " +
                "Fdisable_time, Foperator_id, Fall_judger, Fall_judger_ciid, Fwho_confirm, " +
                "Fwho_confirm_ciid, Fwho_the_next, Fwho_the_next_ciid, Fwho_refused, " +
                "Fwho_refused_ciid, Fcontent_ids, Fcontent_nums, Fcc, Fcc_ciid, Ffinished, " +
                "Fversion, Fouter_position, Fis_resubmit, Frefuse_chain, Frequest_template_type, " +
                "Frela_people, Frela_people_ciid, Fmanual_ending_time, Frequest_content_last, " +
                "Ffinally_confirmed_time, Ffinally_cc_time, Frequest_ancestor_id, " +
                "Frequest_content_last_total_second, Fnatural_content_time_last, " +
                "Fis_annual_leave, Flatest_approved_time, Fapproval_num, Ftotal_money, " +
                "Fgeneration, Fcontact_submit_time, Fstate, Fproxy_contact_id, Fversion_info, " +
                "Ffinancial_status, Finvoice_status, Ftemplate_ancestor_id, Fprint_count " +
                "from request_flow where Fcompany_id != 7975";
        List<RequestFlow> list = new ArrayList<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
//            ps.setInt(1, companyId);
            logger.info(SQLLogger.logSQL(sql));
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    try {
                        RequestFlow request = new RequestFlow();
                        request.setFrequest_id(rs.getInt(1));
                        request.setFrequest_name(rs.getString(2));
                        request.setFrequest_templet(rs.getInt(3));
                        request.setFrequest_step(rs.getInt(4));
                        request.setFcompany_id(rs.getInt(5));

                        request.setFapplicant(rs.getInt(6));
                        request.setFapplicant_ciid(rs.getInt(7));
                        request.setFcreate_time(getTimeStr(rs.getTimestamp(8)));
                        request.setFchecked_time(rs.getString(9));
                        request.setFcall_back_time(getTimeStr(rs.getTimestamp(10)));

                        request.setFdisable_time(getTimeStr(rs.getTimestamp(11)));
                        request.setFoperator_id(rs.getInt(12));
                        request.setFall_judger(rs.getString(13));
                        request.setFall_judger_ciid(rs.getString(14));
                        request.setFwho_confirm(rs.getString(15));

                        request.setFwho_confirm_ciid(rs.getString(16));
                        request.setFwho_the_next(rs.getString(17));
                        request.setFwho_the_next_ciid(rs.getString(18));
                        request.setFwho_refused(rs.getString(19));
                        request.setFwho_refused_ciid(rs.getString(20));

                        request.setFcontent_ids(rs.getString(21));
                        request.setFcontent_nums(rs.getString(22));
                        request.setFcc(rs.getString(23));
                        request.setFcc_ciid(rs.getString(24));
                        request.setFfinished(rs.getInt(25));

                        request.setFversion(rs.getInt(26));
                        request.setFouter_position(rs.getString(27));
                        request.setFis_resubmit(rs.getInt(28));
                        request.setFrefuse_chain(rs.getString(29));
                        request.setFrequest_template_type(rs.getInt(30));

                        request.setFrela_people(rs.getString(31));
                        request.setFrela_people_ciid(rs.getString(32));
                        request.setFmanual_ending_time(getTimeStr(rs.getTimestamp(33)));
                        request.setFrequest_content_last(rs.getString(34));
                        request.setFfinally_confirmed_time(getTimeStr(rs.getTimestamp(35)));

                        request.setFfinally_cc_time(getTimeStr(rs.getTimestamp(36)));
                        request.setFrequest_ancestor_id(rs.getInt(37));
                        request.setFrequest_content_last_total_second(rs.getString(38));
                        request.setFnatural_content_time_last(rs.getString(39));
                        request.setFis_annual_leave(rs.getInt(40));

                        request.setFlatest_approved_time(getTimeStr(rs.getTimestamp(41)));
                        request.setFapproval_num(rs.getString(42));
                        request.setFtotal_money(rs.getString(43));
                        request.setFgeneration(rs.getInt(44));
                        request.setFcontact_submit_time(rs.getString(45));

                        request.setFstate(rs.getInt(46));
                        request.setFproxy_contact_id(rs.getInt(47));
                        request.setFversion_info(rs.getString(48));
                        request.setFfinancial_status(rs.getInt(49));
                        request.setFinvoice_status(rs.getInt(50));

                        request.setFtemplate_ancestor_id(rs.getInt(51));
                        request.setFprint_count(rs.getInt(52));
                        list.add(request);

                    } catch (Exception e) {
                        logger.error("有问题的request:{}", rs.getInt(1));
                        throw e;
                    }
                }
            }
        }
        return list;
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
