package com.erplus.sync;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.ExpenseDao;
import com.erplus.sync.dao.LeaveDao;
import com.erplus.sync.dao.OutdoorDao;
import com.erplus.sync.dao.OvertimeDao;
import com.erplus.sync.dao.impl.*;
import com.erplus.sync.entity.es.*;
import com.erplus.sync.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.junit.Test;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class SyncDataToEs {

    @Test
    public void syncDataToEs() throws Throwable {
        Connection connection = MysqlConnectionUtils.getMysqlConnection();
        RestHighLevelClient client = EsClientUtils.getEsClient();
        RequestDaoImpl requestDao = new RequestDaoImpl(connection);
        ComponentImpl componentImpl = new ComponentImpl(connection);
        FlowDaoImpl flowDaoImpl = new FlowDaoImpl(connection);
        ParticipantDaoImpl participantDao = new ParticipantDaoImpl(connection);
        CcDaoImpl ccDao = new CcDaoImpl(connection);
        CommentDaoImpl commentDao = new CommentDaoImpl(connection);
        ExpenseDao expenseDao = new ExpenseDaoImpl(connection);
        OutdoorDao outdoorDao = new OutdoorDaoImpl(connection);
        LeaveDao leaveDao = new LeaveDaoImpl(connection);
        OvertimeDao overtimeDao = new OvertimeDaoImpl(connection);
        List<Integer> companyIds = Arrays.asList(9500,9491,9453,9444,9423,9396,9380,9371,9361,9359,9358,9316,9315,9309,9308,9302,9247,9214,9182,9179,9167,9161,9151,9145,9144,9112,9090,9056,9035,9017);
        log.info("需要迁移数据到es公司的数量:{}", companyIds.size());
        try {
            for (Integer companyId : companyIds) {
                //查数据
                log.info("正在迁移数据到es的公司的companyId:{}", companyId);
                List<RequestEsEntity> requests = requestDao.selectOneCompanyAllEsRequest(companyId);
                Map<Integer, List<ComponentEsEntity>> componentMap = componentImpl.selectOneCompanyAllComponent(companyId);
                Map<Integer, List<GroupComponentEsEntity>> componentGroupMap = componentImpl.selectOneCompanyAllGroupComponent(companyId);
                Map<Integer, List<ApprovalFlowEsEntity>> flowMap = flowDaoImpl.selectOneCompanyAllFlow(companyId);
                Map<Integer, List<ParticipantEsEntity>> participantMap = participantDao.selectOneCompanyAllParticipant(companyId);
                Map<Integer, List<RequestFieldEsEntity>> ccMap = ccDao.selectOneCompanyAllCc(companyId);
                Map<Integer, List<CommentEsEntity>> commentMap = commentDao.selectOneCompanyAllComment(companyId);
                Map<Integer, List<ExpenseEsEntity>> expenseMap = expenseDao.selectOneCompanyAllExpense(companyId);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> outdoorMap = outdoorDao.selectOneCompanyAllOutdoor(companyId);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> leaveMap = leaveDao.selectOneCompanyAllLeave(companyId);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> overtimeMap = overtimeDao.selectOneCompanyOvertime(companyId);
                log.info("requestSize:{}", requests.size());
                log.info("componentSize:{}", componentMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("componentGroupSize:{}", componentGroupMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("flowSize:{}", flowMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("participantSize:{}", participantMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("ccSize:{}", ccMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("expenseMap:{}", expenseMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("outdoorMap:{}", outdoorMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("leaveMap:{}", leaveMap.values().stream().map(List::size).reduce(0, Integer::sum));
                log.info("overtimeMap:{}", overtimeMap.values().stream().map(List::size).reduce(0, Integer::sum));

                //上传数据到es
                List<List<RequestEsEntity>> divideList = ListHelper.divideList(requests, 500);
                for (int i = 0; i < divideList.size(); i++) {
                    log.info("==========================companyId:{}正在迁移第{}页的数据==========================", companyId, i + 1);
                    List<RequestEsEntity> requestEsEntities = divideList.get(i);
                    BulkRequest bulkRequest = new BulkRequest();
                    for (RequestEsEntity request : requestEsEntities) {
                        Integer requestId = request.getRequest_id();
                        //组件
                        request.setRequest_content(componentMap.get(requestId));
                        //组件集
                        request.setSys_approval_component_group_value(componentGroupMap.get(requestId));
                        //审批流
                        request.setSys_approval_flow(flowMap.get(requestId));
                        //参与人
                        request.setSys_approval_participant(participantMap.get(requestId));
                        //抄送人
                        request.setRequest_filed(ccMap.get(requestId));
                        //评论
                        request.setRequest_comment(commentMap.get(requestId));


                        //需要汇总的信息
                        SummaryField summaryField = new SummaryField();
                        //需要汇总的组件
                        summaryField.setComponent(ComponentUtils.needSummaryComponent(componentMap.get(requestId), request.getDefault_type()));
                        //请假
                        summaryField.setLeave(leaveMap.get(requestId));
                        //外出
                        summaryField.setOutdoor(outdoorMap.get(requestId));
                        //加班
                        summaryField.setOvertime(overtimeMap.get(requestId));
                        //报销
                        summaryField.setExpense(expenseMap.get(requestId));
                        request.setSummary_field(summaryField);


                        //总报销金额和总加班时长（这两个字段用于检索）
                        List<ExpenseEsEntity> expenses = expenseMap.get(requestId);
                        List<LeaveOvertimeOutdoorEsEntity> overtimes = overtimeMap.get(requestId);
                        if (Utils.isNotEmpty(expenses) || Utils.isNotEmpty(overtimes)) {
                            SearchField searchField = getSearchField(expenses, overtimes);
                            request.setSearch_field(searchField);
                        }


                        IndexRequest indexRequest = new IndexRequest("approval_request_flow");
                        indexRequest.id(requestId.toString()).source(JSONObject.toJSONString(request), XContentType.JSON);
                        bulkRequest.add(indexRequest);
                    }
                    client.bulk(bulkRequest, RequestOptions.DEFAULT);
                }
                log.info("我已经迁移完数据了！companyId:{}", companyId);
                log.info("==========================开始迁移下一个公司的数据==========================");
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            EsClientUtils.closeConnection();
            JschSessionUtils.closeSession();
        }
    }

    private SearchField getSearchField(List<ExpenseEsEntity> expenses, List<LeaveOvertimeOutdoorEsEntity> overtimes) {
        SearchField searchField = new SearchField();
        //报销
        if (Utils.isNotEmpty(expenses)) {
            searchField.setTotal_amount(expenses.get(0).getTotal_amount());
        }
        //加班
        if (Utils.isNotEmpty(overtimes)) {
            int totalDuration = 0;
            for (LeaveOvertimeOutdoorEsEntity overtime : overtimes) {
                totalDuration += overtime.getDuration();
            }
            searchField.setTotal_duration(totalDuration);
        }
        return searchField;
    }

}
