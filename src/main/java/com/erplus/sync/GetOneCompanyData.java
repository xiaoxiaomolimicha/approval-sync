package com.erplus.sync;

import com.erplus.sync.dao.CcDao;
import com.erplus.sync.dao.RequestDao;
import com.erplus.sync.dao.impl.CcDaoImpl;
import com.erplus.sync.dao.impl.RequestDaoImpl;
import com.erplus.sync.entity.RequestFiled;
import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.ListHelper;
import com.erplus.sync.utils.MysqlConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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

            queryBuilder.append(formatObject(requestFiled.getFid())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFrequest_id())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFwho_filed())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFwho_filed_mcid())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFwho_filed_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFfiled_time())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFcompany_id())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFstatus())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFdisable_time())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFcreate_time())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFtemplate_type())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFis_temp())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFcrt_by())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFfiled_status())).append(", ");
            queryBuilder.append(formatObject(requestFiled.getFis_read())).append(");");

            insertQueries.add(queryBuilder.toString());
        }

        return insertQueries;
    }

    public List<String> generateRequestFlowInsertSql(List<RequestFlow> requestFlowList) {
        List<String> insertQueries = new ArrayList<>();

        for (RequestFlow requestFlow : requestFlowList) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("insert into request_flow (Frequest_id, Frequest_name, Frequest_templet, Frequest_step, Fcompany_id, Fapplicant, Fapplicant_ciid, Fcreate_time, Fchecked_time, Fcall_back_time, Fdisable_time, Foperator_id, Fall_judger, Fall_judger_ciid, Fwho_confirm, Fwho_confirm_ciid, Fwho_the_next, Fwho_the_next_ciid, Fwho_refused, Fwho_refused_ciid, Fcontent_ids, Fcontent_nums, Fcc, Fcc_ciid, Ffinished, Fversion, Fouter_position, Fis_resubmit, Frefuse_chain, Frequest_template_type, Frela_people, Frela_people_ciid, Fmanual_ending_time, Frequest_content_last, Ffinally_confirmed_time, Ffinally_cc_time, Frequest_ancestor_id, Frequest_content_last_total_second, Fnatural_content_time_last, Fis_annual_leave, Flatest_approved_time, Fapproval_num, Ftotal_money, Fgeneration, Fcontact_submit_time, Fstate, Fproxy_contact_id, Fversion_info, Ffinancial_status, Finvoice_status, Ftemplate_ancestor_id, Fprint_count) values (");

            queryBuilder.append(formatObject(requestFlow.getFrequest_id())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrequest_name())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrequest_templet())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrequest_step())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcompany_id())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFapplicant())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFapplicant_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcreate_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFchecked_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcall_back_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFdisable_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFoperator_id())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFall_judger())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFall_judger_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFwho_confirm())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFwho_confirm_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFwho_the_next())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFwho_the_next_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFwho_refused())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFwho_refused_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcontent_ids())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcontent_nums())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcc())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcc_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFfinished())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFversion())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFouter_position())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFis_resubmit())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrefuse_chain())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrequest_template_type())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrela_people())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrela_people_ciid())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFmanual_ending_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrequest_content_last())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFfinally_confirmed_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFfinally_cc_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrequest_ancestor_id())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFrequest_content_last_total_second())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFnatural_content_time_last())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFis_annual_leave())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFlatest_approved_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFapproval_num())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFtotal_money())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFgeneration())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFcontact_submit_time())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFstate())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFproxy_contact_id())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFversion_info())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFfinancial_status())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFinvoice_status())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFtemplate_ancestor_id())).append(", ");
            queryBuilder.append(formatObject(requestFlow.getFprint_count())).append(");");

            // Add the query to the list
            insertQueries.add(queryBuilder.toString());
        }

        return insertQueries;
    }

    public void writeToTxt(List<String> lines, String filePath) {
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
