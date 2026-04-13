package com.example.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 省市区简称工具类
 * 从 Excel 文件读取省市区简称数据
 */
@Component
public class ProvinceCityAreaAbbreviationUtil {

    // 省名 -> 简称映射
    private static final Map<String, String> PROVINCE_TO_SHORT = new HashMap<>();
    // 省名集合
    private static final Set<String> PROVINCE_NAMES = new HashSet<>();
    // 城市简称映射
    private static final Map<String, String> CITY_TO_SHORT = new HashMap<>();
    // 区县全称 -> 简称映射
    private static final Map<String, String> AREA_TO_SHORT = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            // 从 Excel 文件读取数据
            ClassPathResource resource = new ClassPathResource("省市区简称.xlsx");
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    EasyExcel.read(inputStream, new RegionAbbreviationReadListener())
                            .autoCloseStream(true)
                            .ignoreEmptyRow(true)
                            .headRowNumber(1)
                            .sheet()
                            .doRead();
                }
            }
        } catch (IOException e) {
            System.err.println("读取省市区简称 Excel 文件失败：" + e.getMessage());
            // 如果文件读取失败，使用默认数据
            initDefaultData();
        }
    }

    /**
     * 初始化默认数据（当 Excel 文件读取失败时使用）
     */
    private void initDefaultData() {
        // 直辖市
        PROVINCE_TO_SHORT.put("北京市", "京");
        PROVINCE_TO_SHORT.put("天津市", "津");
        PROVINCE_TO_SHORT.put("上海市", "沪");
        PROVINCE_TO_SHORT.put("重庆市", "渝");
        PROVINCE_NAMES.addAll(PROVINCE_TO_SHORT.keySet());

        // 省份
        String[][] provinces = {
            {"河北省", "冀"}, {"山西省", "晋"}, {"辽宁省", "辽"}, {"吉林省", "吉"},
            {"黑龙江省", "黑"}, {"江苏省", "苏"}, {"浙江省", "浙"}, {"安徽省", "皖"},
            {"福建省", "闽"}, {"江西省", "赣"}, {"山东省", "鲁"}, {"河南省", "豫"},
            {"湖北省", "鄂"}, {"湖南省", "湘"}, {"广东省", "粤"}, {"海南省", "琼"},
            {"四川省", "川"}, {"贵州省", "黔"}, {"云南省", "云"}, {"陕西省", "陕"},
            {"甘肃省", "甘"}, {"青海省", "青"}, {"台湾省", "台"},
            {"内蒙古自治区", "蒙"}, {"广西壮族自治区", "桂"}, {"西藏自治区", "藏"},
            {"宁夏回族自治区", "宁"}, {"新疆维吾尔自治区", "新"}, {"香港特别行政区", "港"},
            {"澳门特别行政区", "澳"}
        };

        for (String[] province : provinces) {
            if (!PROVINCE_TO_SHORT.containsKey(province[0])) {
                PROVINCE_TO_SHORT.put(province[0], province[1]);
                PROVINCE_NAMES.add(province[0]);
            }
        }
    }

    /**
     * 替换名称中的省市区简称
     * @param originalName 原始名称
     * @return 替换后的名称
     */
    public static String replaceRegionAbbreviations(String originalName) {
        if (originalName == null || originalName.trim().isEmpty()) {
            return originalName;
        }

        String result = originalName;

        // 替换省份
        for (Map.Entry<String, String> entry : PROVINCE_TO_SHORT.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }

        // 替换城市
        for (Map.Entry<String, String> entry : CITY_TO_SHORT.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }

        // 替换区县（按长度降序，优先替换长的）
        List<Map.Entry<String, String>> areaList = new ArrayList<>(AREA_TO_SHORT.entrySet());
        areaList.sort((a, b) -> b.getKey().length() - a.getKey().length());
        for (Map.Entry<String, String> entry : areaList) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * 判断是否为省份名称
     */
    public static boolean isProvinceName(String name) {
        return PROVINCE_NAMES.contains(name);
    }

    /**
     * 获取所有省份简称映射
     */
    public static Map<String, String> getProvinceToShortMap() {
        return new HashMap<>(PROVINCE_TO_SHORT);
    }

    /**
     * 根据省份全称获取简称
     * @param province 省份全称（如：广东省）
     * @return 省份简称（如：粤），如果未找到则返回原值
     */
    public static String getProvinceAbbreviation(String province) {
        if (province == null || province.trim().isEmpty()) {
            return province;
        }
        String trimmedProvince = province.trim();
        // 直接匹配
        if (PROVINCE_TO_SHORT.containsKey(trimmedProvince)) {
            return PROVINCE_TO_SHORT.get(trimmedProvince);
        }
        // 包含匹配（如：广东 -> 粤）
        for (Map.Entry<String, String> entry : PROVINCE_TO_SHORT.entrySet()) {
            String provinceName = entry.getKey();
            if (provinceName.contains(trimmedProvince) || trimmedProvince.contains(provinceName)) {
                return entry.getValue();
            }
        }
        return province;
    }

    /**
     * 获取所有省份简称映射（用于替换操作）
     * @return 省份全称到简称的映射
     */
    public static Map<String, String> getAllProvinceAbbreviations() {
        return new HashMap<>(PROVINCE_TO_SHORT);
    }

    /**
     * 获取城市简称
     * @param city 城市全称
     * @return 城市简称
     */
    public static String getCityAbbreviation(String city) {
        if (city == null || city.trim().isEmpty()) {
            return city;
        }
        String trimmedCity = city.trim();
        if (CITY_TO_SHORT.containsKey(trimmedCity)) {
            return CITY_TO_SHORT.get(trimmedCity);
        }
        // 包含匹配
        for (Map.Entry<String, String> entry : CITY_TO_SHORT.entrySet()) {
            String cityName = entry.getKey();
            if (cityName.contains(trimmedCity) || trimmedCity.contains(cityName)) {
                return entry.getValue();
            }
        }
        return city;
    }

    /**
     * 获取区县简称
     * @param area 区县全称
     * @return 区县简称
     */
    public static String getAreaAbbreviation(String area) {
        if (area == null || area.trim().isEmpty()) {
            return area;
        }
        String trimmedArea = area.trim();
        if (AREA_TO_SHORT.containsKey(trimmedArea)) {
            return AREA_TO_SHORT.get(trimmedArea);
        }
        // 包含匹配
        for (Map.Entry<String, String> entry : AREA_TO_SHORT.entrySet()) {
            String areaName = entry.getKey();
            if (areaName.contains(trimmedArea) || trimmedArea.contains(areaName)) {
                return entry.getValue();
            }
        }
        return area;
    }

    /**
     * Excel 读取监听器
     */
    private static class RegionAbbreviationReadListener implements ReadListener<Map<Integer, String>> {
        private Map<Integer, String> headerMap;

        @Override
        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
            this.headerMap = new HashMap<>();
            for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                String headerName = entry.getValue().getStringValue();
                if (headerName != null) {
                    headerMap.put(entry.getKey(), headerName.trim());
                }
            }
        }

        @Override
        public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
            if (rowData == null || headerMap == null) {
                return;
            }

            String province = null;
            String provinceShort = null;
            String city = null;
            String cityShort = null;
            String area = null;
            String areaShort = null;

            for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                String headerName = headerMap.get(entry.getKey());
                String value = entry.getValue() != null ? entry.getValue().trim() : "";

                if (value.isEmpty()) {
                    continue;
                }

                if ("省".equals(headerName)) {
                    province = value;
                } else if ("province_short".equals(headerName)) {
                    provinceShort = value;
                } else if ("市".equals(headerName)) {
                    city = value;
                } else if ("city_short".equals(headerName)) {
                    cityShort = value;
                } else if ("区县".equals(headerName)) {
                    area = value;
                } else if ("area_short".equals(headerName)) {
                    areaShort = value;
                }
            }

            // 省份映射
            if (province != null && provinceShort != null && !provinceShort.isEmpty()) {
                PROVINCE_TO_SHORT.put(province, provinceShort);
                PROVINCE_NAMES.add(province);
            }

            // 城市映射
            if (city != null && cityShort != null && !cityShort.isEmpty()) {
                CITY_TO_SHORT.put(city, cityShort);
            }

            // 区县映射
            if (area != null && areaShort != null && !areaShort.isEmpty()) {
                AREA_TO_SHORT.put(area, areaShort);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            System.out.println("省市区简称数据加载完成：省份 " + PROVINCE_TO_SHORT.size() +
                             " 个，城市 " + CITY_TO_SHORT.size() +
                             " 个，区县 " + AREA_TO_SHORT.size() + " 个");
        }
    }
}
