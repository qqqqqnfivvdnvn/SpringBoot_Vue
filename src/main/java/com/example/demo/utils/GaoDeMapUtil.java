package com.example.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 高德地图地理编码查询工具类
 * 支持多 key 轮换，用于根据地址获取经纬度坐标
 */
public class GaoDeMapUtil {

    // 高德地图 API 地址
    private static final String AMAP_GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo";

    // 多个 key 列表
    private static final String[] API_KEYS = {
        "5b2f92dd2cad9e00d64b779fdce91598",  // 陈
        "fc464f6074e1d612bab41934e24c4dfb",  // 葛
        "1969087a7d06a715f66c5a93b6702592",  // 何
        "74d0a7437dc1ed1f1cefa8da67b4593b"   // 张
    };

    // 当前使用的 key 索引
    private static int currentKeyIndex = 0;

    // 请求配置（超时 5 秒）
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();

    // JSON 解析器
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 全局复用 HttpClient（避免每次请求创建连接池）
    private static final CloseableHttpClient httpClient = createSharedClient();

    private static CloseableHttpClient createSharedClient() {
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(20);
        connMgr.setDefaultMaxPerRoute(10);
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connMgr)
                .build();
    }

    /**
     * 根据地址获取经纬度坐标
     *
     * @param province 省份/城市
     * @param address  详细地址
     * @return 包含地理编码信息的 Map
     */
    public static Map<String, Object> getAreaLatLng(String province, String address) {
        Map<String, Object> result = createEmptyResult(province, address);

        int maxRetries = API_KEYS.length;

        for (int i = 0; i < maxRetries; i++) {
            String currentKey = getCurrentKey();

            HttpGet httpGet = new HttpGet(AMAP_GEOCODE_URL + "?city=" + encodeUrl(province)
                    + "&address=" + encodeUrl(address)
                    + "&key=" + currentKey
                    + "&output=json");

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                    JsonNode jsonNode = objectMapper.readTree(jsonStr);

                    String infocode = jsonNode.has("infocode") ? jsonNode.get("infocode").asText() : "";

                    if ("10000".equals(infocode)) {
                        // 成功
                        result.put("infocode", infocode);
                        result.put("info", jsonNode.has("info") ? jsonNode.get("info").asText() : "OK");

                        JsonNode geocodes = jsonNode.get("geocodes");
                        if (geocodes != null && geocodes.isArray() && geocodes.size() > 0) {
                            JsonNode geo = geocodes.get(0);
                            result.put("area", geo.has("district") && !geo.get("district").asText().isEmpty()
                                    ? geo.get("district").asText() : geo.has("city") ? geo.get("city").asText() : null);
                            result.put("city", geo.has("city") ? geo.get("city").asText() : null);
                            result.put("province", geo.has("province") ? geo.get("province").asText() : null);
                            result.put("areaid", geo.has("adcode") ? geo.get("adcode").asText() : null);
                            result.put("location", geo.has("location") ? geo.get("location").asText() : null);
                        }
                        return result;
                    } else {
                        // 其他错误，切换 key 重试
                        result.put("infocode", infocode);
                        result.put("info", jsonNode.has("info") ? jsonNode.get("info").asText() : "");
                        switchToNextKey();
                        continue;
                    }
                } else {
                    // HTTP 错误
                    result.put("infocode", "3");
                    result.put("info", "请求失败");
                    return result;
                }
            } catch (Exception e) {
                // 网络异常
                result.put("infocode", "3");
                result.put("info", "请求失败：" + e.getMessage());
                switchToNextKey();
            }
        }

        // 所有 key 都尝试失败
        result.put("infocode", "3");
        result.put("info", "所有 key 都已尝试失败");
        return result;
    }

    /**
     * 创建空的结果 Map
     */
    private static Map<String, Object> createEmptyResult(String province, String address) {
        Map<String, Object> result = new HashMap<>();
        result.put("infocode", null);
        result.put("info", null);
        result.put("location", null);
        result.put("province", province);
        result.put("city", null);
        result.put("area", null);
        result.put("areaid", null);
        result.put("address", address);
        return result;
    }

    /**
     * URL 编码
     */
    private static String encodeUrl(String str) {
        try {
            return java.net.URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 获取当前使用的 key
     */
    private static synchronized String getCurrentKey() {
        return API_KEYS[currentKeyIndex];
    }

    /**
     * 切换到下一个 key
     */
    private static synchronized void switchToNextKey() {
        currentKeyIndex = (currentKeyIndex + 1) % API_KEYS.length;
    }

    /**
     * 获取 key 状态
     */
    public static Map<String, Object> getKeyStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalKeys", API_KEYS.length);
        status.put("currentKeyIndex", currentKeyIndex);
        status.put("currentKey", getCurrentKey());
        return status;
    }

    // 测试方法
    public static void main(String[] args) {
        Map<String, Object> result = getAreaLatLng("广元市", "广元市利州区苴国路 490 号");
        System.out.println(result);
        System.out.println("最终 key 状态：" + getKeyStatus());
    }
}
