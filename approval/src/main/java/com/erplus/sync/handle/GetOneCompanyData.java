package com.erplus.sync.handle;

import com.erplus.sync.dao.CcDao;
import com.erplus.sync.dao.RequestDao;
import com.erplus.sync.dao.impl.CcDaoImpl;
import com.erplus.sync.dao.impl.RequestDaoImpl;
import com.erplus.sync.entity.RequestFiled;
import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.MysqlConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;

import java.nio.file.Files;
import java.sql.Connection;
import java.util.*;

@Slf4j
public class GetOneCompanyData {

    @Test
    public void getRequestFlowData() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            RequestDao requestDao = new RequestDaoImpl(connection);
            List<RequestFlow> requests = requestDao.selectOneCompanyAllRequest(7975);
            List<String> list = generateRequestFlowInsertSql(requests);
            writeToTxt(list, "/Users/macos/Desktop/7975_Request_Flow.sql");
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

    @Test
    public void getCCData() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            CcDao ccDao = new CcDaoImpl(connection);
            List<RequestFiled> requestFileds = ccDao.selectOneCompanyAllRequestFiled(7975);
            log.info("抄送数据的size:{}", requestFileds.size());
            List<String> list = generateRequestFiledInsertSql(requestFileds);
            writeToTxt(list, "/Users/macos/Desktop/7975_Request_Filed.sql");
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }

    public List<String> generateRequestFiledInsertSql(List<RequestFiled> requestFiledList) {
        List<String> insertQueries = new ArrayList<>();

        for (RequestFiled requestFiled : requestFiledList) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("insert into request_filed (Fid, Frequest_id, Fwho_filed, Fwho_filed_mcid, Fwho_filed_ciid, Ffiled_time, Fcompany_id, Fstatus, Fdisable_time, Fcreate_time, Ftemplate_type, Fis_temp, Fcrt_by, Ffiled_status, Fis_read) values (");

            queryBuilder.append(formatObject(requestFiled.getId())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getRequestId())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getWhoFiled())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getWhoFiledMcid())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getWhoFiledCiid())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFiledTime())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getCompanyId())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getStatus())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getDisableTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getCreateTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getTemplateType())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getIsTemp())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getCrtBy())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFiledStatus())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getIsRead())).append(");");


            insertQueries.add(queryBuilder.toString());
        }

        return insertQueries;
    }

    public List<String> generateRequestFlowInsertSql(List<RequestFlow> requestFlowList) {
        List<String> insertQueries = new ArrayList<>();

        for (RequestFlow requestFlow : requestFlowList) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("insert into request_flow (Frequest_id, Frequest_name, Frequest_templet, Frequest_step, Fcompany_id, Fapplicant, Fapplicant_ciid, Fcreate_time, Fchecked_time, Fcall_back_time, Fdisable_time, Foperator_id, Fall_judger, Fall_judger_ciid, Fwho_confirm, Fwho_confirm_ciid, Fwho_the_next, Fwho_the_next_ciid, Fwho_refused, Fwho_refused_ciid, Fcontent_ids, Fcontent_nums, Fcc, Fcc_ciid, Ffinished, Fversion, Fouter_position, Fis_resubmit, Frefuse_chain, Frequest_template_type, Frela_people, Frela_people_ciid, Fmanual_ending_time, Frequest_content_last, Ffinally_confirmed_time, Ffinally_cc_time, Frequest_ancestor_id, Frequest_content_last_total_second, Fnatural_content_time_last, Fis_annual_leave, Flatest_approved_time, Fapproval_num, Ftotal_money, Fgeneration, Fcontact_submit_time, Fstate, Fproxy_contact_id, Fversion_info, Ffinancial_status, Finvoice_status, Ftemplate_ancestor_id, Fprint_count) values (");

            queryBuilder.append(formatObject(requestFlow.getRequestId())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRequestName())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getTemplateId())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRequestStep())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getCompanyId())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getApplicant())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getApplicantCiid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getCreateTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getCheckedTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getCallBackTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getDisableTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getOperatorId())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getAllJudger())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getAllJudgerCiid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getWhoConfirm())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getWhoConfirmCiid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getWhoTheNext())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getWhoTheNextCiid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getWhoRefused())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getWhoRefusedCiid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getContentIds())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getContentNums())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getCc())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getCcCiid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFinished())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getVersion())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getOuterPosition())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getIsResubmit())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRefuseChain())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRequestTemplateType())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRelaPeople())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRelaPeopleCiid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getManualEndingTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRequestContentLast())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFinallyConfirmedTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFinallyCcTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRequestAncestorId())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getRequestContentLastTotalSecond())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getNaturalContentTimeLast())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getIsAnnualLeave())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getLatestApprovedTimeStr())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getApprovalNum())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getTotalMoney())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getGeneration())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getContactSubmitTime())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getState())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getProxyContactId())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getVersionInfo())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFinancialStatus())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getInvoiceStatus())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getTemplateAncestorId())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getPrintCount())).append(");");


            // Add the query to the list
            insertQueries.add(queryBuilder.toString());
        }

        return insertQueries;
    }

    public static void writeToTxt(List<String> lines, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(new File(filePath).toPath()), "UTF-8"))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // 换行
            }
            log.info("数据成功写入文件: " + filePath);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            log.info("写入文件时发生错误: " + e.getMessage());
        }
    }

    public String formatObject(Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof String) {
            return "'" + object + "'";
        }
        return object.toString();
    }

    

}
