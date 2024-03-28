package com.erplus.sync.utils;

import com.jcraft.jsch.Session;
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
    private static RestHighLevelClient esClient;

    public static RestHighLevelClient getEsClient() throws Throwable {
        if (esClient != null) {
            return esClient;
        }
        Properties properties = JschSessionUtils.getProperties();
        Session session = JschSessionUtils.getJschSession(properties);

        String esHost = properties.getProperty("esHost");
        int esPort = 9200;

        // 设置端口转发
        session.setPortForwardingL(ES_LOCAL_PORT, esHost, esPort);

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getProperty("esUsername"), properties.getProperty("esPassword")));

        esClient = new RestHighLevelClient(RestClient.builder(
                        new HttpHost("127.0.0.1", ES_LOCAL_PORT))
                .setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(1000)
                        .setSocketTimeout(4000)));

        return esClient;
    }

    public static void closeConnection() {
        try {
            if (esClient != null) {
                esClient.close();
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        }

    }
}