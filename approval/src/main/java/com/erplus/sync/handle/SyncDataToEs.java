package com.erplus.sync.handle;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.*;
import com.erplus.sync.dao.impl.*;
import com.erplus.sync.entity.Constants;
import com.erplus.sync.entity.es.*;
import com.erplus.sync.entity.template.SimpleTemplate;
import com.erplus.sync.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;

import java.sql.Connection;
import java.util.*;

@Slf4j
public class SyncDataToEs {


    /**
     * flink本地同步数据步骤：
     * 一、当flink服务器有没有资源，意思就是不能再启动一个任务
     * 1、es中新建一个索引
     * 2、调用syncDataToEsOnlyCreate()方法同步全部公司的审批数据到es的新索引
     * 3、关闭旧的flink同步审批数据到es的任务，再开启新的任务
     * 4、再调用syncDataToEsCreateOrUpdate()弥补全部公司在停掉flink那段时间可能缺少的审批数据变动
     * 5、在调用syncDataToEsCreateOrUpdate()方法时候可能会发生版本冲突，因为加了乐观锁，发生了版本冲突的审批需要再次同步
     * 6、再调用syncDataToEsCreateOrUpdateByRequestId()方法重新同步发生了版本冲突的审批，直到没有审批发生版本冲突为止
     * 7、审批工程检索es数据的索引切换成新的索引，上完代码过段时间没问题后再删掉旧的es索引
     *
     *
     * 二、当flink服务器有资源，意思就是可以再启动一个任务
     * 1、es中新建一个索引
     * 2、启动新的flink任务
     * 3、调用syncDataToEsOnlyCreate()方法同步全部公司的审批数据到es的新索引
     * 4、如果needAgainHandleRequestIds中有未同步的审批，就调用syncDataToEsOnlyCreateByRequestIds()方法，直到全部审批同步到es中
     * 理论上不会出现未同步的审批，出现的情况一般都是审批的组件数据出了问题
     * 5、审批工程检索es数据的索引切换成新的索引，上完代码过段时间没问题后再删掉旧的es索引
     */


    /**
     * 这个方法只允许往es中添加不存在的审批
     * 使用场景：es索引数据结构发生变化，需要迁移全部审批数据到新的索引上
     */
    @Test
    public void syncDataToEsOnlyCreate() throws Throwable {
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
        List<String> needAgainHandleRequestIds = new ArrayList<>();
        try {
//            List<Integer> companyIds = Arrays.asList(7975);
            List<Integer> companyIds = ApiUtils.getNotDeathCompanyIds("YmUzN2Q1M2VhN2YxZTU1ZDQyOGZiYWEwZWZmMTNlMjJhYWU2MzRjZmQ4NzJiOGUwMDI0YTVlNTk2ZTY0MmRhMg");
            String createTime = null;
            log.info("需要迁移数据到es公司的数量:{}", companyIds.size());
            for (int i = 0; i < companyIds.size(); i++) {
                Integer companyId = companyIds.get(i);
                //查数据
                log.info("正在迁移数据到es的公司的companyId:{}", companyId);
                List<RequestEsEntity> requests = requestDao.selectOneCompanyAllEsRequest(companyId, createTime);
                Map<Integer, List<ComponentEsEntity>> componentMap = componentImpl.selectOneCompanyAllComponent(companyId, createTime);
                Map<Integer, List<GroupComponentEsEntity>> componentGroupMap = componentImpl.selectOneCompanyAllGroupComponent(companyId, createTime);
                Map<Integer, List<ApprovalFlowEsEntity>> flowMap = flowDaoImpl.selectOneCompanyAllFlow(companyId, createTime);
                Map<Integer, List<ParticipantEsEntity>> participantMap = participantDao.selectOneCompanyAllParticipant(companyId, createTime);
                Map<Integer, List<RequestFieldEsEntity>> ccMap = ccDao.selectOneCompanyAllCc(companyId, createTime);
                Map<Integer, List<CommentEsEntity>> commentMap = commentDao.selectOneCompanyAllComment(companyId, createTime);
                Map<Integer, List<ExpenseEsEntity>> expenseMap = expenseDao.selectOneCompanyAllExpense(companyId, createTime);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> outdoorMap = outdoorDao.selectOneCompanyAllOutdoor(companyId, createTime);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> leaveMap = leaveDao.selectOneCompanyAllLeave(companyId, createTime);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> overtimeMap = overtimeDao.selectOneCompanyOvertime(companyId, createTime);
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
                for (int j = 0; j < divideList.size(); j++) {
                    log.info("==========================companyId:{}正在迁移第{}页的数据==========================", companyId, j + 1);
                    List<RequestEsEntity> requestEsEntities = divideList.get(j);
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

                        IndexRequest indexRequest = new IndexRequest(Constants.approvalIndexName);
                        indexRequest.id(requestId.toString()).source(JSONObject.toJSONString(request), XContentType.JSON);
                        indexRequest.opType(DocWriteRequest.OpType.CREATE);//这里表示如果es存在这条审批就不处理，不存在就在es中新建这条审批
                        bulkRequest.add(indexRequest);
                    }
                    BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                    for (BulkItemResponse response : bulkResponse.getItems()) {
                        RestStatus status = response.status();
                        switch (status) {
                            case CONFLICT:
                            case CREATED:
                                break;
                            default:
                                log.error("未知的状态！{}， requestId:{}，response:{}", status, response.getId(), JSONObject.toJSONString(response));
                                needAgainHandleRequestIds.add(response.getId());
                                break;
                        }
                    }
                }
                log.info("我已经迁移完数据了！companyId:{}", companyId);
                log.info("==========================开始迁移下一个公司的数据==========================");
                log.info("==========================还剩{}个公司未迁移数据==========================", companyIds.size() - (i + 1));
            }
            log.info("需要重新同步的审批ids:{}", String.join(",", needAgainHandleRequestIds));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            EsClientUtils.closeClient();
            JschSessionUtils.closeSession();
        }
    }

    /**
     * 往es中添加不存在的审批和更新已存在的审批
     * 使用场景：
     * 1、es中索引数据结构发生变化，且flink没有新的资源启动新的任务
     * 2、flink任务因为某些原因挂掉后，弥补挂掉期间缺失的审批数据变化
     */
    @Test
    public void syncDataToEsCreateOrUpdate() throws Throwable {
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
        List<Integer> companyIds = Arrays.asList(7975);
        //crateTime这个条件要设置为null，如果加上了createTime，就意味着只能查询createTime之后的审批，如果用户操作了createTime之前审批，但是你没查出来，就无法弥补了
        //之前加createTime这个条件没考虑清楚，但懒得删代码了，所以createTime就放在这，但还是需要说明一下，免得用了导致数据不一致
        String createTime = null;
        log.info("需要迁移数据到es公司的数量:{}", companyIds.size());
        Set<Integer> needAgainHandleRequestIds = new HashSet<>(); //需要重新处理的审批ids
        try {
            for (Integer companyId : companyIds) {
                //查数据
                log.info("正在迁移数据到es的公司的companyId:{}", companyId);
                List<RequestEsEntity> requests = requestDao.selectOneCompanyAllEsRequest(companyId, createTime);
                Map<Integer, List<ComponentEsEntity>> componentMap = componentImpl.selectOneCompanyAllComponent(companyId, createTime);
                Map<Integer, List<GroupComponentEsEntity>> componentGroupMap = componentImpl.selectOneCompanyAllGroupComponent(companyId, createTime);
                Map<Integer, List<ApprovalFlowEsEntity>> flowMap = flowDaoImpl.selectOneCompanyAllFlow(companyId, createTime);
                Map<Integer, List<ParticipantEsEntity>> participantMap = participantDao.selectOneCompanyAllParticipant(companyId, createTime);
                Map<Integer, List<RequestFieldEsEntity>> ccMap = ccDao.selectOneCompanyAllCc(companyId, createTime);
                Map<Integer, List<CommentEsEntity>> commentMap = commentDao.selectOneCompanyAllComment(companyId, createTime);
                Map<Integer, List<ExpenseEsEntity>> expenseMap = expenseDao.selectOneCompanyAllExpense(companyId, createTime);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> outdoorMap = outdoorDao.selectOneCompanyAllOutdoor(companyId, createTime);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> leaveMap = leaveDao.selectOneCompanyAllLeave(companyId, createTime);
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> overtimeMap = overtimeDao.selectOneCompanyOvertime(companyId, createTime);
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
                    BulkRequest updateBulkRequest = new BulkRequest();
                    BulkRequest cerateBulkRequest = new BulkRequest();
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

                        GetRequest getRequest = new GetRequest(Constants.approvalIndexName, String.valueOf(requestId));
                        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

                        if (getResponse.isExists()) {
                            /**
                             * 该审批在es中已存在就设置乐观锁
                             * 为什么要设置乐观锁，且还要再更新
                             * 因为在你没有上新的flink任务的时候，没办法监控到审批数据到变化
                             * 当你从mysql中查到审批数据到本地后，如果用户在这期间操作了审批，导致审批数据发生变化
                             * 而你同步过去的到es的数据就变成了过时了的数据
                             * 就比如你查询mysql到本地的某个审批开始是未批准的，但是用户后面批准了该审批，mysql中该审批已是已批准的状态，但是你同步到es的是未批准的审批状态，就导致了数据不一致
                             * 所以，所有已在es中存在了的数据就还要再走一次update操作，保证与mysql中的数据一致
                             * 且在更新了时候发生了版本冲突的审批，还要再次同步，保证es与mysql中数据一致
                             */
                            UpdateRequest updateRequest = new UpdateRequest(Constants.approvalIndexName, requestId.toString());
                            updateRequest.doc(JSONObject.toJSONString(request), XContentType.JSON);
                            updateRequest.setIfSeqNo(getResponse.getSeqNo());
                            updateRequest.setIfPrimaryTerm(getResponse.getPrimaryTerm());
                            updateBulkRequest.add(updateRequest);
                        } else {
                            /**
                             * 为什么es中会出现不存在该审批的情况，因为在你查询某个公司所有审批数据到本地后，这个公司的某人申请了一条审批
                             * 但你那时候没有又没有开启新的flink任务，无法同步新的审批到es中，就导致了es中不存在该审批
                             * indexRequest.opType(DocWriteRequest.OpType.CREATE)相当与redis的setnx操作
                             * 为什么要做setnx操作
                             * 假设你现在查询到的这个审批是未批准的，在你查询到该数据到本地后，用户马上批准了该审批，因为这时候新的flink任务已经启动了
                             * flink会监控到数据发生变化，然后flink那边发现该审批在es中不存在，就会调用selectMysqlDataSyncToEs()方法，查询该审批所有相关的最新数据，也就是已批准的审批状态
                             * 然后flink再同步到es中，如果flink比你先一步同步数据到es，而你不加setnx操作就会覆盖flink同步操作，相当于你把过时的数据覆盖了最新的数据，这就造成了数据不一致
                             */
                            IndexRequest indexRequest = new IndexRequest(Constants.approvalIndexName).id(requestId.toString());
                            indexRequest.source(JSONObject.toJSONString(request), XContentType.JSON);
                            indexRequest.opType(DocWriteRequest.OpType.CREATE);
                            cerateBulkRequest.add(indexRequest);
                        }
                    }
                    BulkResponse createBulkResponse = client.bulk(cerateBulkRequest, RequestOptions.DEFAULT);
                    for (BulkItemResponse response : createBulkResponse.getItems()) {
                        RestStatus status = response.status();
                        switch (status) {
                            case CREATED:
                                //CREATED状态是期望的状态，无需处理
                            case CONFLICT:
                                break;
                            default:
                                log.error("未知的状态！{}， requestId:{}，response:{}", status, response.getId(), JSONObject.toJSONString(response));
                                needAgainHandleRequestIds.add(Integer.parseInt(response.getId()));
                                break;
                        }
                    }

                    BulkResponse updateBulkResponse = client.bulk(updateBulkRequest, RequestOptions.DEFAULT);
                    for (BulkItemResponse response : updateBulkResponse.getItems()) {
                        RestStatus status = response.status();
                        switch (status) {
                            case CONFLICT:
                                needAgainHandleRequestIds.add(Integer.parseInt(response.getId()));
                                break;
                            case OK:
                                break;
                            default:
                                log.error("未知的状态！{}， requestId:{}，response:{}", status, response.getId(), JSONObject.toJSONString(response));
                                break;
                        }
                    }
                }

                log.info("我已经迁移完数据了！companyId:{}", companyId);
                log.info("==========================开始迁移下一个公司的数据==========================");
            }
            log.info("同步数据时候发生版本冲突，需要重新同步的审批requestIds:{}", JSONObject.toJSONString(needAgainHandleRequestIds));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
        }
    }

    @Test
    public void syncDataToEsCreateOrUpdateByRequestId() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            RestHighLevelClient client = EsClientUtils.getEsClient();
            RequestDao requestDao = new RequestDaoImpl(connection);
            ComponentDao componentImpl = new ComponentImpl(connection);
            FlowDao flowDaoImpl = new FlowDaoImpl(connection);
            ParticipantDao participantDao = new ParticipantDaoImpl(connection);
            CcDao ccDao = new CcDaoImpl(connection);
            CommentDao commentDao = new CommentDaoImpl(connection);
            ExpenseDao expenseDao = new ExpenseDaoImpl(connection);
            OutdoorDao outdoorDao = new OutdoorDaoImpl(connection);
            LeaveDao leaveDao = new LeaveDaoImpl(connection);
            OvertimeDao overtimeDao = new OvertimeDaoImpl(connection);

            List<Integer> needAgainHandleRequestIds = new ArrayList<>();
            BulkRequest updateBulkRequest = new BulkRequest();
            BulkRequest createBulkRequest = new BulkRequest();
            List<Integer> requestIds = Arrays.asList(54421);
            for (Integer requestId : requestIds) {
                RequestEsEntity requestEsEntity = requestDao.selectEsRequestByRequestId(requestId);
                //再次编辑和被删除的审批不同步到es
                if (Objects.equals(1, requestEsEntity.getIs_resubmit()) || Objects.equals(-99, requestEsEntity.getFinished())) {
                    return;
                }
                //组件
                List<ComponentEsEntity> componentEsEntityList = componentImpl.selectComponentByRequestId(requestId);
                requestEsEntity.setRequest_content(componentEsEntityList);
                //组件集
                requestEsEntity.setSys_approval_component_group_value(componentImpl.selectGroupComponentByRequestId(requestId));
                //审批流
                requestEsEntity.setSys_approval_flow(flowDaoImpl.selectFlowByRequestId(requestId));
                //参与人
                requestEsEntity.setSys_approval_participant(participantDao.selectParticipantByRequestId(requestId));
                //抄送人
                requestEsEntity.setRequest_filed(ccDao.selectCCByRequestId(requestId));
                //评论
                requestEsEntity.setRequest_comment(commentDao.selectCommentByRequestId(requestId));

                //需要汇总的信息
                SummaryField summaryField = new SummaryField();
                requestEsEntity.setSummary_field(summaryField);
                Integer defaultType = requestEsEntity.getDefault_type();
                //需要汇总的组件
                summaryField.setComponent(ComponentUtils.needSummaryComponent(componentEsEntityList, defaultType));

                //报销模板
                if (Objects.equals(defaultType, SimpleTemplate.EXPENSES)) {
                    List<ExpenseEsEntity> expenseEsEntities = expenseDao.selectExpenseByRequestId(requestId);
                    summaryField.setExpense(expenseEsEntities);

                    if (Utils.isNotEmpty(expenseEsEntities)) {
                        SearchField searchField = new SearchField();
                        searchField.setTotal_amount(expenseEsEntities.get(0).getTotal_amount());
                        requestEsEntity.setSearch_field(searchField);
                    }
                }

                //加班模板
                if (Objects.equals(defaultType, SimpleTemplate.WORK_OVERTIME)) {
                    List<LeaveOvertimeOutdoorEsEntity> overtimes = overtimeDao.selectOvertimeByRequestId(requestId);
                    summaryField.setOvertime(overtimes);
                    if (Utils.isNotEmpty(overtimes)) {
                        SearchField searchField = new SearchField();
                        int totalDuration = 0;
                        for (LeaveOvertimeOutdoorEsEntity overtime : overtimes) {
                            totalDuration += overtime.getDuration();
                        }
                        searchField.setTotal_duration(totalDuration);
                        requestEsEntity.setSearch_field(searchField);
                    }
                }

                //请假模板
                if (Objects.equals(defaultType, SimpleTemplate.LEAVE)) {
                    summaryField.setLeave(leaveDao.selectLeaveByRequestId(requestId));
                }

                //外出模板
                if (Objects.equals(defaultType, SimpleTemplate.OUTDOOR)) {
                    summaryField.setOutdoor(outdoorDao.selectOutdoorByRequestId(requestId));
                }

                GetRequest getRequest = new GetRequest(Constants.approvalIndexName, String.valueOf(requestId));
                GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

                if (getResponse.isExists()) {
                    UpdateRequest updateRequest = new UpdateRequest(Constants.approvalIndexName, requestId.toString());
                    updateRequest.doc(JSONObject.toJSONString(requestEsEntity), XContentType.JSON);
                    updateRequest.setIfSeqNo(getResponse.getSeqNo());
                    updateRequest.setIfPrimaryTerm(getResponse.getPrimaryTerm());
                    updateBulkRequest.add(updateRequest);
                } else {
                    IndexRequest indexRequest = new IndexRequest(Constants.approvalIndexName).id(requestId.toString());
                    indexRequest.source(JSONObject.toJSONString(requestEsEntity), XContentType.JSON);
                    indexRequest.opType(DocWriteRequest.OpType.CREATE);
                    createBulkRequest.add(indexRequest);
                }
            }

            BulkResponse createBulkResponse = client.bulk(createBulkRequest, RequestOptions.DEFAULT);
            for (BulkItemResponse response : createBulkResponse.getItems()) {
                RestStatus status = response.status();
                switch (status) {
                    case CREATED:
                    case CONFLICT:
                        break;
                    default:
                        log.error("未知的状态！{}， requestId:{}，response:{}", status, response.getId(), JSONObject.toJSONString(response));
                        needAgainHandleRequestIds.add(Integer.parseInt(response.getId()));
                        break;
                }
            }

            BulkResponse updateBulkResponse = client.bulk(updateBulkRequest, RequestOptions.DEFAULT);
            for (BulkItemResponse response : updateBulkResponse.getItems()) {
                RestStatus status = response.status();
                switch (status) {
                    case CONFLICT:
                        needAgainHandleRequestIds.add(Integer.parseInt(response.getId()));
                        break;
                    case OK:
                        break;
                    default:
                        log.error("未知的状态！{}， requestId:{}，response:{}", status, response.getId(), JSONObject.toJSONString(response));
                        needAgainHandleRequestIds.add(Integer.parseInt(response.getId()));
                        break;
                }
            }

            log.info("需要再次同步的审批ids:{}", JSONObject.toJSONString(needAgainHandleRequestIds));
        } catch (Throwable e) {
            log.error(e.getMessage());
        } finally {
            JschSessionUtils.closeAll();
        }
    }

    @Test
    public void syncDataToEsOnlyCreateByRequestIds() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            RestHighLevelClient client = EsClientUtils.getEsClient();
            RequestDao requestDao = new RequestDaoImpl(connection);
            ComponentDao componentImpl = new ComponentImpl(connection);
            FlowDao flowDaoImpl = new FlowDaoImpl(connection);
            ParticipantDao participantDao = new ParticipantDaoImpl(connection);
            CcDao ccDao = new CcDaoImpl(connection);
            CommentDao commentDao = new CommentDaoImpl(connection);
            ExpenseDao expenseDao = new ExpenseDaoImpl(connection);
            OutdoorDao outdoorDao = new OutdoorDaoImpl(connection);
            LeaveDao leaveDao = new LeaveDaoImpl(connection);
            OvertimeDao overtimeDao = new OvertimeDaoImpl(connection);
            List<Integer> needAgainHandleRequestIds = new ArrayList<>();

            List<Integer> requestIds = Arrays.asList(87012,3549203,3582833,3609452,3627822,3656392,3656687,3681591,3712115,3731541,3737935,3843853,3916863,4030127,4070496,4071816,4100484,4236492,4251717,4292867,4372270,4409067,4436290,4518063,4564287,4571354,4572092,4633422,4641541,4801171,4805623,5325689,5325805,12376369,14080456,16986504);
            log.info("需要重新同步审批的数量size:{}", requestIds.size());
            List<List<Integer>> divideList = ListHelper.divideList(requestIds, 20);
            log.info("分页后的数量size:{}", divideList.size());
            for (int i = 0; i < divideList.size(); i++) {
                log.info("==========================正在重新同步第{}页的审批==========================", i + 1);
                List<Integer> requestIdList = divideList.get(i);
                //查数据
                log.info("正在重新同步到es的审批requestId:{}", JSONObject.toJSONString(requestIdList));
                List<RequestEsEntity> requestEsEntities = requestDao.selectEsRequestByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<ComponentEsEntity>> componentMap = componentImpl.selectComponentByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<GroupComponentEsEntity>> componentGroupMap = componentImpl.selectGroupComponentByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<ApprovalFlowEsEntity>> flowMap = flowDaoImpl.selectFlowByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<ParticipantEsEntity>> participantMap = participantDao.selectParticipantByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<RequestFieldEsEntity>> ccMap = ccDao.selectCCByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<CommentEsEntity>> commentMap = commentDao.selectCommentByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<ExpenseEsEntity>> expenseMap = expenseDao.selectExpenseByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> outdoorMap = outdoorDao.selectOutdoorByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> leaveMap = leaveDao.selectLeaveByRequestIds(ListHelper.list2string(requestIdList));
                Map<Integer, List<LeaveOvertimeOutdoorEsEntity>> overtimeMap = overtimeDao.selectOvertimeByRequestIds(ListHelper.list2string(requestIdList));

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

                    IndexRequest indexRequest = new IndexRequest(Constants.approvalIndexName).id(requestId.toString());
                    indexRequest.source(JSONObject.toJSONString(request), XContentType.JSON);
                    indexRequest.opType(DocWriteRequest.OpType.CREATE); //设置只新增限制，如果es中已存在，就无需处理
                    bulkRequest.add(indexRequest);
                }
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                for (BulkItemResponse response : bulkResponse.getItems()) {
                    RestStatus status = response.status();
                    switch (status) {
                        case CREATED:
                        case CONFLICT:
                            //状态为冲突就说明es中已存在改审批了，无需再处理
                            break;
                        default:
                            needAgainHandleRequestIds.add(Integer.parseInt(response.getId()));
                            log.error("status:{}", status);
                            log.error("response:{}", JSONObject.toJSONString(response));
                            break;
                    }
                }
                log.info("==========================开始重新同步下一页的审批==========================");
            }
            log.info("需要重新同步的审批的size:{}", needAgainHandleRequestIds.size());
            log.info("需要重新同步的审批requestIds:{}", JSONObject.toJSONString(needAgainHandleRequestIds));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            JschSessionUtils.closeAll();
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
