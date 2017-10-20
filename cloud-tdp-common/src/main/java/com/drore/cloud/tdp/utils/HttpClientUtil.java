package com.drore.cloud.tdp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/10  15:42.
 */
public class HttpClientUtil {

    public static String postJson(String url, JSONObject params) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(JSON.toJSONString(params), "utf-8"));
            httpPost.setHeader("Content-Type", "application/json");
            CloseableHttpResponse response;
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            //防止中文乱码，统一utf-8格式
            String body = EntityUtils.toString(entity, "UTF-8");
            return body;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String httpGet(String get_url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URL url = new URL(get_url);
            URI uri = new URI(url.getProtocol(), (url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort())), url.getPath(), url.getQuery(), null);
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
