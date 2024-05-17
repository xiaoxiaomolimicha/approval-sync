package com.erplus.sync.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;


public class HttpUtilsWithoutLog {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtilsWithoutLog.class);
    public static final String LOG_OUT_MESSAGE = "Server returned HTTP response code: 401 for URL";

    public static String get(String url, String token) throws Exception {
        return get(url, token, 60000);
    }

    public static String get(String url, String token, int timeout) throws Exception {
        logger.info("url:get {}", url);
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                result.append(new String(line.getBytes(), StandardCharsets.UTF_8));
            }


            String response = result.toString();
            try {
                if (response.length() > 2000) {
                    logger.debug("http response:{}", response);
                } else if (response.startsWith("[")) {
                    logger.info("http response:{}", JSONObject.parseArray(response));
                } else if (response.startsWith("{")) {
                    logger.info("http response:{}", JSONObject.parseObject(response));
                } else {
                    logger.info("http response:{}", response);
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                logger.info("http response:{}", response);
            }
            return response;
        } catch (Exception var17) {
            logger.error(var17.getMessage(), var17);
            throw new Exception(var17.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception var16) {
                logger.error(var16.getMessage(), var16);
            }

        }
    }

    public static String post(String url, String param, String token) throws Exception {
        return post(url, param, token, 10000);
    }

    public static String post(String url, String param, String token, int timeout) throws Exception {
        logger.info("url:post {}", url);
        logger.info("param:{}", param);
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
            if (Utils.isNotNull(param) && !"null".equals(param) && !"{}".equals(param)){
                out = new PrintWriter(connection.getOutputStream());
                out.print(param);
                out.flush();
            }
            InputStreamReader reader;
            if (connection.getResponseCode() >= 400) {
                reader = new InputStreamReader(connection.getErrorStream());
            } else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            in = new BufferedReader(reader);
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            String response = result.toString();
            try {
                if (response.length() > 2000) {
                    logger.info("http response:{}", result.length());
                } else if (response.startsWith("[")) {
                    logger.info("http response:{}", JSONObject.parseArray(response));
                } else if (response.startsWith("{")) {
                    logger.info("http response:{}", JSONObject.parseObject(response));
                } else {
                    logger.info("http response:{}", response);
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                logger.info("http response:{}", response);
            }
            return response;
        } catch (Exception var20) {
            logger.error(var20.getMessage(), var20);
            throw new Exception(var20.getMessage(), var20);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException var19) {
                logger.error(var19.getMessage(), var19);
            }

        }
    }

    public static String patch(String url, String param, String token) throws Exception {
        logger.info("url:patch {}", url);
        logger.info("param:{}", param);
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
            out = new PrintWriter(connection.getOutputStream());
            out.print(param);
            out.flush();
            InputStreamReader reader;
            if (connection.getResponseCode() >= 400) {
                reader = new InputStreamReader(connection.getErrorStream());
            } else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            in = new BufferedReader(reader);
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            String response = result.toString();
            try {
                if (response.length() > 2000) {
                    logger.info("http response:{}", result.length());
                } else if (response.startsWith("[")) {
                    logger.info("http response:{}", JSONObject.parseArray(response));
                } else if (response.startsWith("{")) {
                    logger.info("http response:{}", JSONObject.parseObject(response));
                } else {
                    logger.info("http response:{}", response);
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                logger.info("http response:{}", response);
            }
            return response;
        } catch (Exception var19) {
            var19.printStackTrace();
            logger.error(var19.getMessage(), var19);
            throw new Exception(var19.getMessage(), var19);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException var18) {
                logger.error(var18.getMessage(), var18);
            }

        }
    }

}
