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
 *
 * Excel 数据格式：
 * | province | province_short | city | city_short | area | area_short |
 * |----------|----------------|------|------------|------|------------|
 * | 河北省   | 河北           | 石家庄市 | 石家庄 | 长安区 | 长安 |
 * | 河北省   | 河北           | 石家庄市 | 石家庄 | 藁城区 | 藁城 |
 * | 广东省   | 广东           | 深圳市   | 深圳   | 南山区 | 南山 |
 *
 * 数据结构：
 * - PROVINCE_TO_SHORT: province -> province_short（如：河北省 -> 河北）
 * - PROVINCE_NAMES: 所有省份名称集合
 * - PROVINCE_CITY_TO_SHORT: province_short -> {city -> city_short}（如：河北 -> {石家庄市 -> 石家庄}）
 * - PROVINCE_AREA_TO_SHORT: province_short -> {area -> area_short}（如：河北 -> {长安区 -> 长安}）
 *
 * 替换逻辑（需要传入省份参数）：
 * 第一轮：全称 -> 简称
 *   1. 替换省份全称 -> 简称（如：河北省 -> 河北）
 *   2. 替换城市全称 -> 简称（只替换指定省份的城市，如：石家庄市 -> 石家庄）
 *   3. 替换区县全称 -> 简称（只替换指定省份的区县，如：长安区 -> 长安）
 *
 * 第二轮：简称 -> 空
 *   1. 替换省份简称 -> 空（如：河北 -> 空）
 *   2. 替换城市简称 -> 空（只替换指定省份的城市，如：石家庄 -> 空）
 *   3. 替换区县简称 -> 空（只替换指定省份的区县，如：长安 -> 空）
 *
 * 示例：
 *   originalName = "河北石家庄长安某某医院", province = "河北省"
 *   第一轮后："河北石家庄长安某某医院" -> "河北石家庄长安某某医院"（省市区已在名称中）
 *   实际替换：河北省石家庄市 -> 河北 -> 河北石家庄 -> 河北石家庄长安
 *   第二轮后："某某医院"
 *
 * 注意：只有在传入省份参数时，才会替换该省份对应的城市和区县简称
 */
@Component
public class ProvinceCityAreaAbbreviationUtil {

    // 省名 -> 简称映射
    private static final Map<String, String> PROVINCE_TO_SHORT = new HashMap<>();
    // 省名集合
    private static final Set<String> PROVINCE_NAMES = new HashSet<>();
    // 省份简称 -> 该省的城市简称映射
    private static final Map<String, Map<String, String>> PROVINCE_CITY_TO_SHORT = new HashMap<>();
    // 省份简称 -> 该省的区县简称映射
    private static final Map<String, Map<String, String>> PROVINCE_AREA_TO_SHORT = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            // 从 Excel 文件读取数据
            ClassPathResource resource = new ClassPathResource("province_city_area_short.xlsx");
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
        }
    }

    /**
     * 替换名称中的省市区简称（带省份参数，只替换该省份的省市区）
     *
     * 替换顺序：
     * 第一轮：全称 -> 简称（省级 -> 市级 -> 区级）
     *   1. 替换省份全称 -> 简称（如：河北省 -> 河北）
     *   2. 替换城市全称 -> 简称（只替换指定省份的城市，如：石家庄市 -> 石家庄）
     *   3. 替换区县全称 -> 简称（只替换指定省份的区县，按长度降序优先替换长的）
     *
     * 第二轮：简称 -> 空（省级 -> 市级 -> 区级）
     *   1. 替换省份简称 -> 空（如：河北 -> 空）
     *   2. 替换城市简称 -> 空（只替换指定省份的城市，如：石家庄 -> 空）
     *   3. 替换区县简称 -> 空（只替换指定省份的区县，按简称长度降序）
     *
     * 示例：
     *   originalName = "河北省石家庄市赞皇县某某医院", province = "河北省"
     *   第一轮：河北省 -> 河北，石家庄市 -> 石家庄，赞皇县 -> 赞皇
     *   结果："河北石家庄赞皇某某医院"
     *   第二轮：河北 -> 空，石家庄 -> 空，赞皇 -> 空
     *   结果："某某医院"
     *
     * @param originalName 原始名称
     * @param province 省份名称（如：河北省），为空时不替换城市和区县简称
     * @return 替换后的名称
     */
    public static String replaceRegionAbbreviations(String originalName, String province) {
        if (originalName == null || originalName.trim().isEmpty()) {
            return originalName;
        }

        String result = originalName;

        // ==================== 第一轮：替换全称 ====================
        // 1. 替换省份全称 → 简称
        for (Map.Entry<String, String> entry : PROVINCE_TO_SHORT.entrySet()) {
            String fullName = entry.getKey();  // 如：广东省
            String shortName = entry.getValue(); // 如：粤
            if (result.contains(fullName)) {
                result = result.replace(fullName, shortName);
            }
        }

        // 2. 替换城市全称 → 简称（只替换指定省份的城市）
        if (province != null && !province.trim().isEmpty()) {
            // 获取省份简称
            String provinceShort = PROVINCE_TO_SHORT.get(province);
            if (provinceShort == null) {
                // 尝试匹配：如"广东" -> "粤"
                for (Map.Entry<String, String> entry : PROVINCE_TO_SHORT.entrySet()) {
                    if (entry.getKey().contains(province) || province.contains(entry.getKey())) {
                        provinceShort = entry.getValue();
                        break;
                    }
                }
            }

            // 根据省份简称获取该省的城市映射
            if (provinceShort != null && PROVINCE_CITY_TO_SHORT.containsKey(provinceShort)) {
                Map<String, String> cityMap = PROVINCE_CITY_TO_SHORT.get(provinceShort);
                for (Map.Entry<String, String> entry : cityMap.entrySet()) {
                    String fullName = entry.getKey();  // 如：深圳市
                    String shortName = entry.getValue(); // 如：深
                    if (result.contains(fullName)) {
                        result = result.replace(fullName, shortName);
                    }
                }
            }

            // 3. 替换区县全称 → 简称（只替换指定省份的区县，按长度降序，优先替换长的）
            if (provinceShort != null && PROVINCE_AREA_TO_SHORT.containsKey(provinceShort)) {
                List<Map.Entry<String, String>> areaList = new ArrayList<>(PROVINCE_AREA_TO_SHORT.get(provinceShort).entrySet());
                areaList.sort((a, b) -> b.getKey().length() - a.getKey().length());
                for (Map.Entry<String, String> entry : areaList) {
                    String fullName = entry.getKey();  // 如：南山区
                    String shortName = entry.getValue(); // 如：南
                    if (result.contains(fullName)) {
                        result = result.replace(fullName, shortName);
                    }
                }
            }
        }

        // ==================== 第二轮：替换简称 ====================
        // 1. 替换省份简称 → 去除（如：粤 → 空）
        for (Map.Entry<String, String> entry : PROVINCE_TO_SHORT.entrySet()) {
            String shortName = entry.getValue(); // 如：粤
            if (result.contains(shortName)) {
                result = result.replace(shortName, "");
            }
        }

        // 2. 替换城市简称 → 去除（只替换指定省份的城市）
        if (province != null && !province.trim().isEmpty()) {
            String provinceShort = PROVINCE_TO_SHORT.get(province);
            if (provinceShort == null) {
                for (Map.Entry<String, String> entry : PROVINCE_TO_SHORT.entrySet()) {
                    if (entry.getKey().contains(province) || province.contains(entry.getKey())) {
                        provinceShort = entry.getValue();
                        break;
                    }
                }
            }

            // 根据省份简称获取该省的城市映射
            if (provinceShort != null && PROVINCE_CITY_TO_SHORT.containsKey(provinceShort)) {
                Map<String, String> cityMap = PROVINCE_CITY_TO_SHORT.get(provinceShort);
                for (Map.Entry<String, String> entry : cityMap.entrySet()) {
                    String shortName = entry.getValue(); // 如：深
                    if (result.contains(shortName)) {
                        result = result.replace(shortName, "");
                    }
                }
            }

            // 3. 替换区县简称 → 去除（只替换指定省份的区县，按长度降序）
            if (provinceShort != null && PROVINCE_AREA_TO_SHORT.containsKey(provinceShort)) {
                List<Map.Entry<String, String>> areaShortList = new ArrayList<>(PROVINCE_AREA_TO_SHORT.get(provinceShort).entrySet());
                areaShortList.sort((a, b) -> b.getValue().length() - a.getValue().length());
                for (Map.Entry<String, String> entry : areaShortList) {
                    String shortName = entry.getValue(); // 如：南
                    if (result.contains(shortName)) {
                        result = result.replace(shortName, "");
                    }
                }
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
     * 获取城市简称（根据省份）
     * @param province 省份全称或简称
     * @param city 城市全称
     * @return 城市简称
     */
    public static String getCityAbbreviation(String province, String city) {
        if (city == null || city.trim().isEmpty()) {
            return city;
        }
        if (province == null || province.trim().isEmpty()) {
            return city;
        }

        // 获取省份简称
        String provinceShort = getProvinceAbbreviation(province);

        // 根据省份简称获取该省的城市映射
        if (provinceShort != null && PROVINCE_CITY_TO_SHORT.containsKey(provinceShort)) {
            Map<String, String> cityMap = PROVINCE_CITY_TO_SHORT.get(provinceShort);
            if (cityMap.containsKey(city)) {
                return cityMap.get(city);
            }
            // 包含匹配
            for (Map.Entry<String, String> entry : cityMap.entrySet()) {
                if (entry.getKey().contains(city) || city.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return city;
    }

    /**
     * 获取区县简称（根据省份）
     * @param province 省份全称或简称
     * @param area 区县全称
     * @return 区县简称
     */
    public static String getAreaAbbreviation(String province, String area) {
        if (area == null || area.trim().isEmpty()) {
            return area;
        }
        if (province == null || province.trim().isEmpty()) {
            return area;
        }

        // 获取省份简称
        String provinceShort = getProvinceAbbreviation(province);

        // 根据省份简称获取该省的区县映射
        if (provinceShort != null && PROVINCE_AREA_TO_SHORT.containsKey(provinceShort)) {
            Map<String, String> areaMap = PROVINCE_AREA_TO_SHORT.get(provinceShort);
            if (areaMap.containsKey(area)) {
                return areaMap.get(area);
            }
            // 包含匹配
            for (Map.Entry<String, String> entry : areaMap.entrySet()) {
                if (entry.getKey().contains(area) || area.contains(entry.getKey())) {
                    return entry.getValue();
                }
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

                if ("province".equals(headerName)) {
                    province = value;
                } else if ("province_short".equals(headerName)) {
                    provinceShort = value;
                } else if ("city".equals(headerName)) {
                    city = value;
                } else if ("city_short".equals(headerName)) {
                    cityShort = value;
                } else if ("area".equals(headerName)) {
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

            // 城市映射（按省份分组）
            if (city != null && cityShort != null && !cityShort.isEmpty() && provinceShort != null) {
                PROVINCE_CITY_TO_SHORT.computeIfAbsent(provinceShort, k -> new HashMap<>()).put(city, cityShort);
            }

            // 区县映射（按省份分组）
            if (area != null && areaShort != null && !areaShort.isEmpty() && provinceShort != null) {
                PROVINCE_AREA_TO_SHORT.computeIfAbsent(provinceShort, k -> new HashMap<>()).put(area, areaShort);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            System.out.println("省市区简称数据加载完成：省份 " + PROVINCE_TO_SHORT.size() +
                             " 个，城市 " + PROVINCE_CITY_TO_SHORT.size() +
                             " 个省份有数据，区县 " + PROVINCE_AREA_TO_SHORT.size() + " 个省份有数据");
        }
    }
}
