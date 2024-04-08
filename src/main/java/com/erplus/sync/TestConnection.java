package com.erplus.sync;

import com.alibaba.fastjson.JSONObject;
import com.erplus.sync.dao.*;
import com.erplus.sync.dao.impl.*;
import com.erplus.sync.entity.RequestFiled;
import com.erplus.sync.entity.RequestFlow;
import com.erplus.sync.entity.es.*;
import com.erplus.sync.utils.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.*;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
public class TestConnection {


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

    @Test
    public void updateOneVirtualDate() {
        try {
            RestHighLevelClient client = EsClientUtils.getEsClient();
            int firstRequestId = 10100000;
            int requestId = firstRequestId + 1;
            IndexRequest indexRequest = new IndexRequest("approval_request_flow").id(String.valueOf(requestId));
            RequestEsEntity requestEsEntity = new RequestEsEntity();
            requestEsEntity.setRequest_id(requestId);
            requestEsEntity.setCompany_id(200000);
            List<ComponentEsEntity> componentEsEntities = new ArrayList<>();
            ComponentEsEntity componentEsEntity = new ComponentEsEntity();
            componentEsEntity.setUnique_id(1);
            componentEsEntity.setNum(1);
            componentEsEntity.setFloat_value(null);
            componentEsEntities.add(componentEsEntity);
            requestEsEntity.setRequest_content(componentEsEntities);
            indexRequest.source(JSONObject.toJSONString(requestEsEntity), XContentType.JSON);
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            log.info("status:{}", response.status());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            EsClientUtils.closeConnection();
            JschSessionUtils.closeSession(EsClientUtils.ES_LOCAL_PORT);
        }
    }

    @Test
    public void uploadVirtualDate(){
        try {
            RestHighLevelClient esClient = EsClientUtils.getEsClient();
            int forSize = 100000;
            int firstRequestId = 10000000;
            List<RequestEsEntity> requestEsEntities = new ArrayList<>();
            for (int i = 1; i <= forSize; i++) {
                RequestEsEntity requestEsEntity = new RequestEsEntity();
                int requestId = firstRequestId + i;
                requestEsEntity.setRequest_id(requestId);
                requestEsEntity.setCompany_id(200000);
                List<ComponentEsEntity> componentEsEntities = new ArrayList<>();
                ComponentEsEntity componentEsEntity = new ComponentEsEntity();
                componentEsEntity.setUnique_id(1);
                componentEsEntity.setNum(1);
                componentEsEntity.setFloat_value(Float.parseFloat(String.valueOf(i)));
                componentEsEntities.add(componentEsEntity);
                requestEsEntity.setRequest_content(componentEsEntities);
                requestEsEntities.add(requestEsEntity);
            }
            for (List<RequestEsEntity> esEntities : ListHelper.divideList(requestEsEntities, 500)) {
                BulkRequest bulkRequest = new BulkRequest();
                for (RequestEsEntity esEntity : esEntities) {
                    IndexRequest indexRequest = new IndexRequest("approval_request_flow");
                    indexRequest.id(esEntity.getRequest_id().toString()).source(JSONObject.toJSONString(esEntity), XContentType.JSON.JSON);
                    bulkRequest.add(indexRequest);
                }
                BulkResponse bulk = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                log.info("status:{}", bulk.status());
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            EsClientUtils.closeConnection();
            JschSessionUtils.closeSession(EsClientUtils.ES_LOCAL_PORT);
        }
    }

    @Test
    public void searchSortData() {
        try {
            RestHighLevelClient esClient = EsClientUtils.getEsClient();
            SearchRequest searchRequest = new SearchRequest("approval_request_flow");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.filter(QueryBuilders.termQuery("company_id", 200000));
            sourceBuilder.query(boolQueryBuilder);

            FieldSortBuilder nestedSort = new FieldSortBuilder("request_content.floatValue")
                    .sortMode(SortMode.MAX)
                    .order(SortOrder.DESC)
                    .setNestedSort(new NestedSortBuilder("request_content")
                            .setFilter(QueryBuilders.termQuery("request_content.num", 1)));
            sourceBuilder.sort(nestedSort);
            searchRequest.source(sourceBuilder);

            sourceBuilder.trackTotalHits(true);
            sourceBuilder.from(0);
            sourceBuilder.size(1000);
            sourceBuilder.fetchSource(new String[]{"request_id"}, null);
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            log.info("total:{}", hits.getTotalHits().value);
            for (SearchHit hit : hits) {
                log.info("sourceData:{}", hit.getSourceAsString());
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            EsClientUtils.closeConnection();
            JschSessionUtils.closeSession(EsClientUtils.ES_LOCAL_PORT);
        }
    }

    @Test
    public void saveData() throws IOException {
        RestHighLevelClient client = getLocalEsClient();
        try {
            int forSize = 100000;
            List<JSONObject> dataList = new ArrayList<>();
            for (int i = 1; i <= forSize; i++) {
                JSONObject data = new JSONObject();
                data.put("request_id", i);
                data.put("company_id", 12);
                List<JSONObject> requestContents = new ArrayList<>();
                JSONObject requestContent1 = new JSONObject();
                requestContent1.put("id", 20000 + i);
                requestContent1.put("num", 1);
                requestContent1.put("unique_id", 1);
                requestContent1.put("type", 3);
                requestContent1.put("value", String.valueOf(i));
                requestContent1.put("floatValue", i);

                JSONObject requestContent2 = new JSONObject();
                requestContent2.put("id", 40000 + i);
                requestContent2.put("num", 2);
                requestContent2.put("unique_id", 2);
                requestContent2.put("type", 3);
                requestContent2.put("value", String.valueOf(i * 2));
                requestContent2.put("floatValue", i * 2);
                requestContents.add(requestContent1);
                requestContents.add(requestContent2);
                data.put("request_content", requestContents);
                dataList.add(data);
            }

            List<List<JSONObject>> lists = ListHelper.divideList(dataList, 10000);
            for (int i = 1; i <= lists.size(); i++) {
                int template_ancestor_id = i * 10;
                List<JSONObject> list = lists.get(i - 1);
                BulkRequest bulkRequest = new BulkRequest();
                for (JSONObject data : list) {
                    IndexRequest indexRequest = new IndexRequest("my_index5").id(data.getString("request_id"));
                    data.put("template_ancestor_id", template_ancestor_id);
                    indexRequest.source(JSONObject.toJSONString(data), XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            client.close();
        }
    }

    @Test
    public void testDQL() {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            sourceBuilder.query(boolQuery);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("request_content.date_value");
            rangeQuery.gte("2012-12-12");
            rangeQuery.lte("2013-12-12");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("request_content", rangeQuery, ScoreMode.None);
            boolQuery.filter(nestedQuery);
            log.info("DQL:{}", sourceBuilder.toString());
        } catch (Throwable e) {

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
