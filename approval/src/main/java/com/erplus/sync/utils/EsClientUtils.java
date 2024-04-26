package com.erplus.sync.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Properties;

@Slf4j
public class EsClientUtils {

    public static int ES_LOCAL_PORT = 3308;
    private static volatile RestHighLevelClient esClient;

    public static RestHighLevelClient getEsClient() {
        if (esClient == null) {
            synchronized (EsClientUtils.class) {
                if (esClient == null) {
                    // 设置端口转发
                    ForwardPortUtils.forwardEsPort();
                    Properties properties = PropertiesSingleton.getProperties();
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getProperty("esUsername"), properties.getProperty("esPassword")));

                    esClient = new RestHighLevelClient(RestClient.builder(
                                    new HttpHost("127.0.0.1", ES_LOCAL_PORT))
                            .setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider))
                            .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(1000)
                                    .setSocketTimeout(4000)));
                }
            }
        }
        return esClient;
    }

    public static void closeClient() {
        try {
            if (esClient != null) {
                esClient.close();
                log.info("我关闭了:es客户端的连接");
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        }

    }

    public static RestHighLevelClient getLocalEsClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "woslabeosw146867"));

        return new RestHighLevelClient(RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http"))
                .setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(1000)
                        .setSocketTimeout(4000)));
    }
}