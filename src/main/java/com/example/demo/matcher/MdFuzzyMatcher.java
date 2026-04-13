package com.example.demo.matcher;

import com.example.demo.mapper.MdFuzzyMatchMapper;
import com.example.demo.utils.ProvinceCityAreaAbbreviationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主数据模糊匹配器
 * 实现根据省份和名称进行模糊匹配的逻辑
 */
@Component
public class MdFuzzyMatcher {

    @Autowired
    private MdFuzzyMatchMapper mdFuzzyMatchMapper;

    /**
     * 根据省份和名称进行模糊匹配
     * 按照文档逻辑：
     * 1. 先将名称拆分成 %字%字% 格式进行精确匹配
     * 2. 如果匹配不到或多条，用 %名称% 或历史名称匹配
     * 3. 替换省市区简称后重试
     * 4. 最后匹配商业公司表
     *
     * @param province 省份（全称或简称）
     * @param name     机构名称
     * @return 匹配结果，包含 keyid、name、remark；未找到返回 null
     */
    public Map<String, Object> match(String province, String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        // 拆分名称为 %杨%永%发% 格式
        String splitPattern = splitNameToPattern(name);

        List<Map<String, Object>> results;
        Map<String, Object> matchResult = null;

        // 步骤 1: 精确匹配（名称拆分后匹配）
        if (province != null && !province.trim().isEmpty()) {
            results = mdFuzzyMatchMapper.matchHospitalByProvinceAndName(province, splitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPattern + "] 匹配成功");
            }
        }

        // 步骤 2: 放宽条件，使用 %名称% 或历史名称匹配
        String likePattern = "%" + name + "%";
        if (province != null && !province.trim().isEmpty()) {
            results = mdFuzzyMatchMapper.matchHospitalWithHistory(province, likePattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和名称 [" + name + "] 或历史名称匹配成功");
            }
        }

        // 步骤 3: 替换省市区简称后重试
        if (province != null && !province.trim().isEmpty()) {
            String processedName = ProvinceCityAreaAbbreviationUtil.replaceRegionAbbreviations(name);
            String processedSplitPattern = splitNameToPattern(processedName);

            results = mdFuzzyMatchMapper.matchHospitalByProvinceAndName(province, processedSplitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后名称 [" + processedName + "] 匹配成功");
            }

            String processedLikePattern = "%" + processedName + "%";
            results = mdFuzzyMatchMapper.matchHospitalWithHistory(province, processedLikePattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后名称 [" + processedName + "] 或历史名称匹配成功");
            }

            // 商业公司：精确匹配（拆分后的名称）
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(province, processedSplitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后拆分名称 [" + processedSplitPattern + "] 匹配到商业公司");
            }

            // 商业公司：放宽匹配
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndName(province, processedLikePattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和处理后名称 [" + processedName + "] 匹配到商业公司");
            }
        }

        // 步骤 4: 匹配商业公司表
        if (province != null && !province.trim().isEmpty()) {
            // 商业公司：精确匹配（拆分后的名称）
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(province, splitPattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和拆分名称 [" + splitPattern + "] 匹配到商业公司");
            }

            // 商业公司：放宽匹配
            results = mdFuzzyMatchMapper.matchCompanyByProvinceAndName(province, likePattern);
            if (results != null && results.size() == 1) {
                return buildMatchResult(results.get(0), province,
                        "根据省份 [" + province + "] 和名称 [" + name + "] 匹配到商业公司");
            }
        }

        // 无省份的情况：重复上述步骤但不带省份条件
        // 步骤 1: 无省份精确匹配
        results = matchHospitalNoProvince(splitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPattern + "] 匹配成功 (无省份条件)");
        }

        // 步骤 2: 无省份放宽匹配
        results = matchHospitalWithHistoryNoProvince(likePattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据名称 [" + name + "] 或历史名称匹配成功 (无省份条件)");
        }

        // 步骤 3: 无省份处理后名称匹配
        String processedName = ProvinceCityAreaAbbreviationUtil.replaceRegionAbbreviations(name);
        String processedSplitPattern = splitNameToPattern(processedName);
        results = matchHospitalNoProvince(processedSplitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据处理后名称 [" + processedName + "] 匹配成功 (无省份条件)");
        }

        results = matchHospitalWithHistoryNoProvince("%" + processedName + "%");
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据处理后名称 [" + processedName + "] 或历史名称匹配成功 (无省份条件)");
        }

        // 商业公司：精确匹配（拆分后的名称）
        results = matchCompanyNoProvinceSplit(processedSplitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据处理后拆分名称 [" + processedSplitPattern + "] 匹配到商业公司 (无省份条件)");
        }

        // 商业公司：放宽匹配
        results = matchCompanyNoProvince("%" + processedName + "%");
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据处理后名称 [" + processedName + "] 匹配到商业公司 (无省份条件)");
        }

        // 步骤 4: 无省份匹配商业公司
        // 商业公司：精确匹配（拆分后的名称）
        results = matchCompanyNoProvinceSplit(splitPattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据拆分名称 [" + splitPattern + "] 匹配到商业公司 (无省份条件)");
        }

        results = matchCompanyNoProvince(likePattern);
        if (results != null && results.size() == 1) {
            return buildMatchResult(results.get(0), null,
                    "根据名称 [" + name + "] 匹配到商业公司 (无省份条件)");
        }

        return null; // 未找到匹配
    }

    /**
     * 构建匹配结果
     */
    private Map<String, Object> buildMatchResult(Map<String, Object> dbResult, String province, String remark) {
        Map<String, Object> result = new HashMap<>();
        result.put("keyid", dbResult.get("keyid"));
        result.put("name", dbResult.get("name"));
        result.put("remark", remark);
        return result;
    }

    /**
     * 将名称拆分成 %字%字% 格式
     * 例如：杨永发西医诊所 -> %杨%永%发%西%医%诊%所%
     */
    private String splitNameToPattern(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        StringBuilder sb = new StringBuilder("%");
        for (char c : name.toCharArray()) {
            sb.append(c).append("%");
        }
        return sb.toString();
    }

    /**
     * 无省份条件下匹配医院
     */
    private List<Map<String, Object>> matchHospitalNoProvince(String namePattern) {
        return mdFuzzyMatchMapper.matchHospitalByProvinceAndName(null, namePattern);
    }

    /**
     * 无省份条件下匹配医院（包含历史名称）
     */
    private List<Map<String, Object>> matchHospitalWithHistoryNoProvince(String namePattern) {
        return mdFuzzyMatchMapper.matchHospitalWithHistory(null, namePattern);
    }

    /**
     * 无省份条件下匹配商业公司
     */
    private List<Map<String, Object>> matchCompanyNoProvince(String namePattern) {
        return mdFuzzyMatchMapper.matchCompanyByProvinceAndName(null, namePattern);
    }

    /**
     * 无省份条件下匹配商业公司（精确匹配，拆分后的名称）
     */
    private List<Map<String, Object>> matchCompanyNoProvinceSplit(String namePattern) {
        return mdFuzzyMatchMapper.matchCompanyByProvinceAndSplitName(null, namePattern);
    }
}
