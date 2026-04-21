package com.example.demo.utils;

import com.example.demo.mapper.MdFuzzyMatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主数据模糊匹配器
 *
 * 实现医疗机构和商业公司（药店）的模糊匹配逻辑，支持有/无省份条件的多级匹配策略。
 *
 * 匹配流程：
 * 1. 名称过滤：去除特殊字符，只保留字母、数字、汉字
 * 2. 名称拆分：将名称拆分为 %字%字% 格式用于精确匹配
 * 3. 分级匹配：
 *    - 医院匹配（只匹配 hospital 表）
 *    - 药店匹配（只匹配 company 表）
 * 4. 匹配策略：
 *    - 有省份：精确匹配 → 放宽匹配 → 替换省市区简称后重试
 *    - 无省份：精确匹配 → 放宽匹配（不进行省市区简称替换）
 *
 * 匹配模式说明：
 * - 精确匹配：%字%字%字（结尾不加%）
 * - 放宽匹配：%字%字%字%（结尾加%）
 *
 * 匹配成功条件：查询结果有且仅有一条记录
 *
 * 注意：省市区简称替换只在有省份条件时才使用
 */
@Component
public class MdFuzzyMatcher {

    @Autowired
    private MdFuzzyMatchMapper mdFuzzyMatchMapper;

    /**
     * 根据省份和名称进行模糊匹配（不区分类型，匹配所有表）
     * 匹配逻辑按以下优先级进行：
     *
     * 【有省份条件】
     *   一、医疗机构匹配
     *     1. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
     *     2. 放宽匹配：使用拆分名称 + 结尾%（含历史名称匹配）
     *     3. 替换省市区简称后重试：精确匹配 + 放宽匹配
     *   二、药店（商业公司）匹配
     *     4. 替换省市区简称后：精确匹配（拆分名称）+ 放宽匹配
     *     5. 原始名称：精确匹配（拆分名称）+ 放宽匹配
     *
     * 【无省份条件】
     *   三、医疗机构匹配
     *     6. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
     *     7. 放宽匹配：使用拆分名称 + 结尾%（含历史名称匹配）
     *   四、药店（商业公司）匹配
     *     8. 原始名称：精确匹配（拆分名称）+ 放宽匹配
     *
     * 注意：省市区简称替换只在有省份条件时才使用
     *
     * @param province 省份（全称或简称）
     * @param name     机构名称
     * @return 匹配结果，包含 keyid、name、remark；未找到返回 null
     */
    public Map<String, Object> match(String province, String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }




        // 对名称进行过滤，只保留字母、数字和汉字
        String filteredName = filterName(name);
        if (filteredName == null || filteredName.trim().isEmpty()) {
            return null;
        }

        // 拆分名称为 %杨%永%发% 格式
        String splitPattern = splitNameToPattern(filteredName);

        List<Map<String, Object>> results;
        Map<String, Object> matchResult = null;

        // ==================== 有省份条件的匹配 ====================
        if (province != null && !province.trim().isEmpty()) {

            // 一、医疗机构匹配
            // 1. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
            results = mdFuzzyMatchMapper.matchHospitalByProvinceAndName(province, splitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPattern + "] 匹配到医疗机构");
            }

            // 2. 放宽匹配：使用拆分名称 + 结尾%（含历史名称匹配）
            String splitPatternWithTrailing = splitNameToPattern(filteredName, true);
            results = mdFuzzyMatchMapper.matchHospitalWithHistory(province, splitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPatternWithTrailing + "] 或历史名称匹配到医疗机构");
            }

            // 3. 替换省市区简称后重试：精确匹配 + 放宽匹配（只在有省份时才替换）
            String processedName = ProvinceCityAreaAbbreviationUtil.replaceRegionAbbreviations(filteredName, province);
            String processedSplitPattern = splitNameToPattern(processedName);
            String processedSplitPatternWithTrailing = splitNameToPattern(processedName, true);

            results = mdFuzzyMatchMapper.matchHospitalByProvinceAndName(province, processedSplitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPattern + "] 匹配到医疗机构");
            }

            results = mdFuzzyMatchMapper.matchHospitalWithHistory(province, processedSplitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPatternWithTrailing + "] 或历史名称匹配到医疗机构");
            }

            // 二、药店（商业公司）匹配
            // 4. 替换省市区简称后：精确匹配（拆分名称）+ 放宽匹配
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(province, processedSplitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPattern + "] 匹配到药店");
            }

            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndName(province, processedSplitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPatternWithTrailing + "] 匹配到药店");
            }

            // 5. 原始名称：精确匹配（拆分名称）+ 放宽匹配
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(province, splitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPattern + "] 匹配到药店");
            }

            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndName(province, splitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPatternWithTrailing + "] 匹配到药店");
            }
        }

        // ==================== 无省份条件的匹配 ====================

        // 三、医疗机构匹配
        // 6. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
        results = matchHospitalNoProvince(splitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPattern + "] 匹配到医疗机构 (无省份条件)");
        }

        // 7. 放宽匹配：使用拆分名称 + 结尾%（含历史名称匹配）
        String splitPatternWithTrailing2 = splitNameToPattern(filteredName, true);
        results = matchHospitalWithHistoryNoProvince(splitPatternWithTrailing2);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPatternWithTrailing2 + "] 或历史名称匹配到医疗机构 (无省份条件)");
        }

        // 8. 无省份条件，不进行省市区简称替换
        // 四、药店（商业公司）匹配
        // 9. 原始名称：精确匹配（拆分名称）+ 放宽匹配
        results = matchCompanyNoProvinceSplit(splitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPattern + "] 匹配到药店 (无省份条件)");
        }

        results = matchCompanyNoProvince(splitPatternWithTrailing2);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPatternWithTrailing2 + "] 匹配到药店 (无省份条件)");
        }

        return null; // 未找到匹配
    }

    /**
     * 过滤名称，只保留字母、数字、汉字和特殊字符（如○）
     * 对应 SQL Server 函数 RegexReplaceAlnumChinese
     *
     * 保留字符范围：
     * - 大写字母 A-Z (65-90)
     * - 小写字母 a-z (97-122)
     * - 数字 0-9 (48-57)
     * - 特殊符号：○ (9675, 12295)、O (79)
     * - 汉字：13312-19903 (扩展 A 区)、19968-40959 (统一汉字)
     *
     * @param input 输入字符串
     * @return 过滤后的字符串
     */
    private String filterName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            int code = (int) ch;

            // A-Z (65-90)
            if (code >= 65 && code <= 90) {
                output.append(ch);
            }
            // a-z (97-122)
            else if (code >= 97 && code <= 122) {
                output.append(ch);
            }
            // 0-9 (48-57)
            else if (code >= 48 && code <= 57) {
                output.append(ch);
            }
            // ○ (9675)
            else if (code == 9675) {
                output.append(ch);
            }
            // O (79)
            else if (code == 79) {
                output.append(ch);
            }
            // ○ (12295) - CJK 符号
            else if (code == 12295) {
                output.append(ch);
            }
            // 汉字范围：13312-19903 (CJK 统一汉字扩展 A 区等)
            else if (code >= 13312 && code <= 19903) {
                output.append(ch);
            }
            // 汉字范围：19968-40959 (CJK 统一汉字)
            else if (code >= 19968 && code <= 40959) {
                output.append(ch);
            }
            // 其他字符过滤掉
        }

        return output.toString();
    }

    /**
     * 构建匹配结果
     *
     * @param dbResult 数据库查询结果
     * @param province 省份
     * @param remark   匹配说明
     * @return 匹配结果 Map，包含 keyid、name、remark
     */
    private Map<String, Object> buildMatchResult(Map<String, Object> dbResult, String province, String remark) {
        Map<String, Object> result = new HashMap<>();
        result.put("keyid", dbResult.get("keyid"));
        result.put("name", dbResult.get("name"));
        result.put("remark", remark);
        return result;
    }

    /**
     * 将名称拆分成 %字%字% 格式（精确匹配）
     * 用于 SQL LIKE 查询，实现逐字精确匹配
     *
     * 转换示例：
     * - "杨永发西医诊所" → "%杨%永%发%西%医%诊%所"
     * - "仁济医院" → "%仁%济%医%院"
     *
     * @param name 机构名称
     * @return 拆分后的匹配模式
     */
    private String splitNameToPattern(String name) {
        return splitNameToPattern(name, false);
    }

    /**
     * 将名称拆分成 %字%字% 格式
     * 用于 SQL LIKE 查询，实现逐字精确匹配
     *
     * 转换示例：
     * - 精确匹配："杨永发西医诊所" → "%杨%永%发%西%医%诊%所"
     * - 放宽匹配："杨永发西医诊所" → "%杨%永%发%西%医%诊%所%"
     *
     * @param name 机构名称
     * @param withTrailingPercent 是否在结尾添加%，false=精确匹配，true=放宽匹配
     * @return 拆分后的匹配模式
     */
    private String splitNameToPattern(String name, boolean withTrailingPercent) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        StringBuilder sb = new StringBuilder("%");
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
            // 每个字后面都添加%，最后一个字根据 withTrailingPercent 决定
            if (i < chars.length - 1 || withTrailingPercent) {
                sb.append("%");
            }
        }
        return sb.toString();
    }

    /**
     * 无省份条件下匹配医院
     * 调用 matchHospitalByProvinceAndName，传入 null 省份
     *
     * @param namePattern 名称匹配模式（如 %字%字% 或 %名称%）
     * @return 匹配结果列表
     */
    private List<Map<String, Object>> matchHospitalNoProvince(String namePattern) {
        return mdFuzzyMatchMapper.matchHospitalByProvinceAndName(null, namePattern);
    }

    /**
     * 无省份条件下匹配医院（包含历史名称）
     * 调用 matchHospitalWithHistory，传入 null 省份
     *
     * @param namePattern 名称匹配模式（如 %名称%）
     * @return 匹配结果列表
     */
    private List<Map<String, Object>> matchHospitalWithHistoryNoProvince(String namePattern) {
        return mdFuzzyMatchMapper.matchHospitalWithHistory(null, namePattern);
    }

    /**
     * 无省份条件下匹配商业公司
     * 调用 matchCompanyByProvinceAndName，传入 null 省份
     *
     * @param namePattern 名称匹配模式（如 %名称%）
     * @return 匹配结果列表
     */
    private List<Map<String, Object>> matchCompanyNoProvince(String namePattern) {
        return mdFuzzyMatchMapper.matchCompanyByProvinceAndName(null, namePattern);
    }

    /**
     * 无省份条件下匹配商业公司（精确匹配，拆分后的名称）
     * 调用 matchCompanyByProvinceAndSplitName，传入 null 省份
     *
     * @param namePattern 名称匹配模式（如 %字%字%）
     * @return 匹配结果列表
     */
    private List<Map<String, Object>> matchCompanyNoProvinceSplit(String namePattern) {
        return mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(null, namePattern);
    }

    /**
     * 医院专用匹配方法 - 只匹配 hospital 表
     *
     * 匹配流程：
     * 1. 名称过滤：去除特殊字符，只保留字母、数字、汉字，同时过滤"有限责任公司"和"有限公司"
     * 2. 名称拆分：将名称拆分为 %字%字% 格式用于精确匹配
     * 3. 分级匹配：
     *    【有省份】
     *      - 精确匹配：%字%字（结尾不加%）
     *      - 放宽匹配：%字%字%（结尾加%，含历史名称）
     *      - 替换省市区简称后重试：精确匹配 + 放宽匹配
     *    【无省份】
     *      - 精确匹配：%字%字（结尾不加%）
     *      - 放宽匹配：%字%字%（结尾加%，含历史名称）
     *      - 不进行省市区简称替换
     *
     * 匹配成功条件：查询结果有且仅有一条记录
     *
     * @param province 省份（全称或简称）
     * @param name     机构名称
     * @return 匹配结果，包含 keyid、name、remark；未找到返回 null
     */
    public Map<String, Object> matchHospital(String province, String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        // 对名称进行过滤，只保留字母、数字和汉字
        String filteredName = filterName(name);
        if (filteredName == null || filteredName.trim().isEmpty()) {
            return null;
        }

        // 医院匹配时，过滤掉"有限责任公司"和"有限公司"
        String processedName = filteredName
                .replace("有限责任公司", "")
                .replace("有限公司", "");
        if (processedName.isEmpty()) {
            return null;
        }

        // 拆分名称为 %杨%永%发% 格式
        String splitPattern = splitNameToPattern(processedName);

        List<Map<String, Object>> results;
        Map<String, Object> matchResult = null;

        // ==================== 有省份条件的匹配 ====================
        if (province != null && !province.trim().isEmpty()) {
            // 1. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
            results = mdFuzzyMatchMapper.matchHospitalByProvinceAndName(province, splitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPattern + "] 匹配到医院");
            }

            // 2. 放宽匹配：使用拆分名称 + 结尾%（含历史名称匹配）
            String splitPatternWithTrailing = splitNameToPattern(processedName, true);
            results = mdFuzzyMatchMapper.matchHospitalWithHistory(province, splitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPatternWithTrailing + "] 或历史名称匹配到医院");
            }

            // 3. 替换省市区简称后重试：精确匹配 + 放宽匹配（只在有省份时才替换）
            String processedNameWithRegion = ProvinceCityAreaAbbreviationUtil.replaceRegionAbbreviations(processedName, province);
            String processedSplitPattern = splitNameToPattern(processedNameWithRegion);
            String processedSplitPatternWithTrailing = splitNameToPattern(processedNameWithRegion, true);

            results = mdFuzzyMatchMapper.matchHospitalByProvinceAndName(province, processedSplitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPattern + "] 匹配到医院");
            }

            results = mdFuzzyMatchMapper.matchHospitalWithHistory(province, processedSplitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPatternWithTrailing + "] 或历史名称匹配到医院");
            }
        }

        // ==================== 无省份条件的匹配 ====================
        // 4. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
        results = matchHospitalNoProvince(splitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPattern + "] 匹配到医院 (无省份条件)");
        }

        // 5. 放宽匹配：使用拆分名称 + 结尾%（含历史名称匹配）
        String splitPatternWithTrailing = splitNameToPattern(processedName, true);
        results = matchHospitalWithHistoryNoProvince(splitPatternWithTrailing);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPatternWithTrailing + "] 或历史名称匹配到医院 (无省份条件)");
        }

        return null; // 未找到匹配
    }

    /**
     * 药店专用匹配方法 - 只匹配 company 表
     *
     * 匹配流程：
     * 1. 名称过滤：去除特殊字符，只保留字母、数字、汉字
     * 2. 名称拆分：将名称拆分为 %字%字% 格式用于精确匹配
     * 3. 分级匹配：
     *    【有省份】
     *      - 精确匹配：%字%字（结尾不加%）
     *      - 放宽匹配：%字%字%（结尾加%）
     *      - 替换省市区简称后重试：精确匹配 + 放宽匹配
     *    【无省份】
     *      - 精确匹配：%字%字（结尾不加%）
     *      - 放宽匹配：%字%字%（结尾加%）
     *      - 不进行省市区简称替换
     *
     * 匹配成功条件：查询结果有且仅有一条记录
     *
     * @param province 省份（全称或简称）
     * @param name     机构名称
     * @return 匹配结果，包含 keyid、name、remark；未找到返回 null
     */
    public Map<String, Object> matchDrugStore(String province, String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        // 对名称进行过滤，只保留字母、数字和汉字
        String filteredName = filterName(name);
        if (filteredName == null || filteredName.trim().isEmpty()) {
            return null;
        }

        // 拆分名称为 %杨%永%发% 格式
        String splitPattern = splitNameToPattern(filteredName);

        List<Map<String, Object>> results;

        // ==================== 有省份条件的匹配 ====================
        if (province != null && !province.trim().isEmpty()) {
            // 1. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(province, splitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPattern + "] 匹配到药店");
            }

            // 2. 放宽匹配：使用拆分名称 + 结尾%
            String splitPatternWithTrailing = splitNameToPattern(filteredName, true);
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndName(province, splitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPatternWithTrailing + "] 匹配到药店");
            }

            // 3. 替换省市区简称后重试：精确匹配 + 放宽匹配（只在有省份时才替换）
            String processedName = ProvinceCityAreaAbbreviationUtil.replaceRegionAbbreviations(filteredName, province);
            String processedSplitPattern = splitNameToPattern(processedName);
            String processedSplitPatternWithTrailing = splitNameToPattern(processedName, true);

            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(province, processedSplitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPattern + "] 匹配到药店");
            }

            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndName(province, processedSplitPatternWithTrailing);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPatternWithTrailing + "] 匹配到药店");
            }
        }

        // ==================== 无省份条件的匹配 ====================
        // 4. 精确匹配：名称拆分为 %字%字 格式匹配（结尾不加%）
        results = matchCompanyNoProvinceSplit(splitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPattern + "] 匹配到药店 (无省份条件)");
        }

        // 5. 放宽匹配：使用拆分名称 + 结尾%
        String splitPatternWithTrailing = splitNameToPattern(filteredName, true);
        results = matchCompanyNoProvince(splitPatternWithTrailing);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPatternWithTrailing + "] 匹配到药店 (无省份条件)");
        }

        return null; // 未找到匹配
    }
}
