package com.example.demo.apidata;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

public class ApiHaosen {



    public String callExternalAppealApi() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://192.168.33.9:8000/appeal_data");

            // 设置合理超时（10秒）
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(1800000)
                    .setSocketTimeout(1800000)
                    .build();
            request.setConfig(config);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // 获取完整响应内容
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            }
        } catch (Exception e) {

            return "接口调用失败";
        }
    }


    public String callExternalUpdateApi() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://192.168.33.9:8000/update_data");

            // 设置合理超时（10秒）
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(10)
                    .setSocketTimeout(10)
                    .build();
            request.setConfig(config);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // 获取完整响应内容
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            }
        } catch (Exception e) {

            return "接口调用失败";
        }
    }




    public String callExternalCleanDataApi() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://192.168.33.9:8000/clean_data");

            // 设置合理超时（10秒）
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(1800000)
                    .setSocketTimeout(1800000)
                    .build();
            request.setConfig(config);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // 获取完整响应内容
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            }
        } catch (Exception e) {

            return "接口调用失败";
        }
    }



}
