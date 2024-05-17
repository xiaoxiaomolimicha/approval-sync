package com.erplus.sync.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.entity.es.RequestEsEntity;
import com.erplus.sync.entity.es.RequestFieldEsEntity;
import com.erplus.sync.utils.SQLLogger;
import com.erplus.sync.dao.RequestDao;
import org.apache.commons.lang3.StringUtils;
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
    public List<RequestEsEntity> selectOneCompanyAllEsRequest(Integer companyId, String createTime) throws SQLException {
        String sql = "select Frequest_id, Fcompany_id, Fapproval_num, Frequest_name, Fapplicant_ciid, " +
                "Fproxy_contact_id, Ffinished, Ffinancial_status, Finvoice_status, Ftemplate_ancestor_id, " +
                "Frequest_templet, Fcreate_time, Ffinally_confirmed_time, Ffinally_cc_time, Fapplicant, " +
                "Frequest_template_type " +
                "from request_flow where Fcompany_id = ? and Ffinished != -99 and Fis_resubmit = 0";
        if (StringUtils.isNotBlank(createTime)) {
            sql = sql + " and Fcreate_time >= '" + createTime + "'";
        }
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
                        request.setRequestId(rs.getInt(1));
                        request.setRequestName(rs.getString(2));
                        request.setTemplateId(rs.getInt(3));
                        request.setRequestStep(rs.getInt(4));
                        request.setCompanyId(rs.getInt(5));

                        request.setApplicant(rs.getInt(6));
                        request.setApplicantCiid(rs.getInt(7));
                        request.setCreateTime(rs.getTimestamp(8));
                        request.setCreateTimeStr(getTimeStr(rs.getTimestamp(8)));
                        request.setCheckedTime(rs.getString(9));
                        request.setCallBackTime(rs.getTimestamp(10));
                        request.setCallBackTimeStr(getTimeStr(rs.getTimestamp(10)));

                        request.setDisableTime(rs.getTimestamp(11));
                        request.setDisableTimeStr(getTimeStr(rs.getTimestamp(11)));
                        request.setOperatorId(rs.getInt(12));
                        request.setAllJudger(rs.getString(13));
                        request.setAllJudgerCiid(rs.getString(14));
                        request.setWhoConfirm(rs.getString(15));

                        request.setWhoConfirmCiid(rs.getString(16));
                        request.setWhoTheNext(rs.getString(17));
                        request.setWhoTheNextCiid(rs.getString(18));
                        request.setWhoRefused(rs.getString(19));
                        request.setWhoRefusedCiid(rs.getString(20));

                        request.setContentIds(rs.getString(21));
                        request.setContentNums(rs.getString(22));
                        request.setCc(rs.getString(23));
                        request.setCcCiid(rs.getString(24));
                        request.setFinished(rs.getInt(25));

                        request.setVersion(rs.getInt(26));
                        request.setOuterPosition(rs.getString(27));
                        request.setIsResubmit(rs.getInt(28));
                        request.setRefuseChain(rs.getString(29));
                        request.setRequestTemplateType(rs.getInt(30));

                        request.setRelaPeople(rs.getString(31));
                        request.setRelaPeopleCiid(rs.getString(32));
                        request.setManualEndingTime(rs.getTimestamp(33));
                        request.setManualEndingTimeStr(getTimeStr(rs.getTimestamp(33)));
                        request.setRequestContentLast(rs.getString(34));
                        request.setFinallyConfirmedTime(rs.getTimestamp(35));
                        request.setFinallyConfirmedTimeStr(getTimeStr(rs.getTimestamp(35)));

                        request.setFinallyCcTime(rs.getTimestamp(36));
                        request.setFinallyCcTime(rs.getTimestamp(36));
                        request.setRequestAncestorId(rs.getInt(37));
                        request.setRequestContentLastTotalSecond(rs.getString(38));
                        request.setNaturalContentTimeLast(rs.getString(39));
                        request.setIsAnnualLeave(rs.getInt(40));

                        request.setLatestApprovedTime(rs.getTimestamp(41));
                        request.setLatestApprovedTimeStr(getTimeStr(rs.getTimestamp(41)));
                        request.setApprovalNum(rs.getString(42));
                        request.setTotalMoney(rs.getString(43));
                        request.setGeneration(rs.getInt(44));
                        request.setContactSubmitTime(rs.getString(45));

                        request.setState(rs.getInt(46));
                        request.setProxyContactId(rs.getInt(47));
                        request.setVersionInfo(rs.getString(48));
                        request.setFinancialStatus(rs.getInt(49));
                        request.setInvoiceStatus(rs.getInt(50));

                        request.setTemplateAncestorId(rs.getInt(51));
                        request.setPrintCount(rs.getInt(52));
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
    public List<RequestEsEntity> selectEsRequestByRequestIds(String requestIds) throws SQLException {
        String sql = "select Frequest_id, Fcompany_id, Fapproval_num, Frequest_name, Fapplicant_ciid, " +
                "Fproxy_contact_id, Ffinished, Ffinancial_status, Finvoice_status, Ftemplate_ancestor_id, " +
                "Frequest_templet, Fcreate_time, Ffinally_confirmed_time, Ffinally_cc_time, Fapplicant, " +
                "Frequest_template_type, Fis_resubmit " +
                "from request_flow where Frequest_id in (" + requestIds +")";
        List<RequestEsEntity> result = new ArrayList<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            logger.info(sql);
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
                    requestEs.setIs_resubmit(rs.getInt(17));
                    result.add(requestEs);
                }
            }
        }
        return result;
    }

    @Override
    public RequestEsEntity selectEsRequestByRequestId(Integer requestId) throws SQLException {
        String sql = "select Frequest_id, Fcompany_id, Fapproval_num, Frequest_name, Fapplicant_ciid, " +
                "Fproxy_contact_id, Ffinished, Ffinancial_status, Finvoice_status, Ftemplate_ancestor_id, " +
                "Frequest_templet, Fcreate_time, Ffinally_confirmed_time, Ffinally_cc_time, Fapplicant, " +
                "Frequest_template_type, Fis_resubmit " +
                "from request_flow where Frequest_id = ?";
        List<RequestEsEntity> result = new ArrayList<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            ps.setInt(1, requestId);
            logger.info(SQLLogger.logSQL(sql, requestId));
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
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
                    requestEs.setIs_resubmit(rs.getInt(17));
                    return requestEs;
                }
            }
        }
        return null;
    }

    @Override
    public List<Integer> selectAllCompanyIds() throws SQLException {
        String sql = "select Fcompany_id from request_flow where Frequest_id > 0 and Fcompany_id > 0 group by Fcompany_id order by Fcompany_id";
        List<Integer> companyIds = new ArrayList<>();
        try (PreparedStatement ps = getPreparedStatement(sql)){
            logger.info(sql);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    companyIds.add(rs.getInt(1));
                }
            }
        }
        return companyIds;
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
