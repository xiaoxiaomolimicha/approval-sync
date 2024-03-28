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
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
public class TestConnection {

    @Test
    public void migrationDataToEs() throws Throwable {
        Integer companyId = 1829;
        Connection connection = MysqlConnectionUtils.getMysqlConnection();
        RestHighLevelClient client = EsClientUtils.getEsClient();
        log.info("正在迁移数据到es的公司的companyId:{}", companyId);
        try {
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

            //查数据
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

                    IndexRequest indexRequest = new IndexRequest("approval_request_flow");
                    indexRequest.id(requestId.toString()).source(JSONObject.toJSONString(request), XContentType.JSON.JSON);
                    bulkRequest.add(indexRequest);
                }
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
            log.info("我已经迁移完数据了！");

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            EsClientUtils.closeConnection();
            JschSessionUtils.closeSession();
        }
    }


    @Test
    public void testConnection() {
        try {
            Connection connection = MysqlConnectionUtils.getMysqlConnection();
            RestHighLevelClient esClient = EsClientUtils.getEsClient();
            String sql = "select Frequest_id from request_flow limit 1";
            try (PreparedStatement ps = connection.prepareStatement(sql)){
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        log.info("requestId:{}", rs.getInt(1));
                    }
                }
            }
            SearchRequest request = new SearchRequest("approval_request_flow");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from(0);
            sourceBuilder.size(10);
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            request.source(sourceBuilder);
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            for (SearchHit hit : response.getHits()) {
                log.info(hit.getSourceAsString());
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        } finally {
            EsClientUtils.closeConnection();
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession();
        }
    }

    @Test
    public void getData() throws Throwable {
        try {
            Connection mysqlConnection = MysqlConnectionUtils.getMysqlConnection();
            ComponentImpl component = new ComponentImpl(mysqlConnection);
            ExpenseDaoImpl expenseDao = new ExpenseDaoImpl(mysqlConnection);
            Map<Integer, List<ComponentEsEntity>> integerListMap = component.selectOneCompanyAllComponent(12);
            Map<Integer, List<ExpenseEsEntity>> expenseMap = expenseDao.selectOneCompanyAllExpense(1829);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            MysqlConnectionUtils.closeConnection();
            JschSessionUtils.closeSession(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        }
    }


    @Test
    public void testHits() throws Throwable{
        RestHighLevelClient esClient = EsClientUtils.getEsClient();
        try {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices("approval_request_flow");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            sourceBuilder.fetchSource(new String[]{"request_id", "summary_field.expense.pay_amount", "summary_field.leave.duration", "summary_field.overtime.duration", "summary_field.outdoor.duration"}, null);
            sourceBuilder.fetchSource(new String[]{"request_id"}, null);
            sourceBuilder.query(QueryBuilders.termQuery("request_id", 19178759));
            TermsAggregationBuilder termsAgg = AggregationBuilders.terms("template_ancestor_id_agg").field("template_ancestor_id").size(1000);
            sourceBuilder.aggregation(termsAgg);
            searchRequest.source(sourceBuilder);
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms aggregations = response.getAggregations().get("template_ancestor_id_agg");
            for (Terms.Bucket bucket : aggregations.getBuckets()) {
                log.info("template_ancestor_id:{}", bucket.getKeyAsNumber().intValue());
                log.info("size:{}", (int) bucket.getDocCount());
                if (bucket.getDocCountError() != 0) {
                    log.error("es聚合template_ancestor_id出现异常size:{}", bucket.getDocCountError());
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            EsClientUtils.closeConnection();
            JschSessionUtils.closeSession(EsClientUtils.ES_LOCAL_PORT);
        }
    }


    private RestHighLevelClient getLocalEsClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "woslabeosw146867"));

        return new RestHighLevelClient(RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http"))
                .setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(1000)
                        .setSocketTimeout(4000)));
    }

}
