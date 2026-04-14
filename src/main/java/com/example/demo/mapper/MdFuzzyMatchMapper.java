package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.MdFuzzyMatchConditionDTO;
import com.example.demo.entity.MdFuzzyMatchBatch;
import com.example.demo.entity.MdFuzzyMatchSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * 主数据模糊匹配 Mapper 接口
 */
@Mapper
@DS("master_sqlserver")
public interface MdFuzzyMatchMapper {

    /**
     * 插入批次记录
     */
    int insertBatch(MdFuzzyMatchBatch batch);

    /**
     * 更新批次状态
     */
    int updateBatchStatus(@Param("batchId") String batchId, @Param("status") Integer status, @Param("message") String message);

    /**
     * 查询批次列表
     */
    List<MdFuzzyMatchBatch> getBatchList(@Param("batchId") String batchId,
                                          @Param("status") Integer status,
                                          @Param("startTime") String startTime,
                                          @Param("endTime") String endTime);

    /**
     * 插入模糊匹配汇总数据
     */
    int insertSummary(MdFuzzyMatchSummary summary);

    /**
     * 查询汇总数据列表
     */
    List<MdFuzzyMatchSummary> getSummaryList(MdFuzzyMatchConditionDTO condition);

    /**
     * 根据省份和名称模糊匹配医院（精确匹配）
     */
    @MapKey("keyid")
    List<Map<String, Object>> matchHospitalByProvinceAndName(@Param("province") String province, @Param("namePattern") String namePattern);

    /**
     * 根据省份和名称模糊匹配医院（包含历史名称）
     */
    @MapKey("keyid")
    List<Map<String, Object>> matchHospitalWithHistory(@Param("province") String province, @Param("namePattern") String namePattern);

    /**
     * 根据省份和名称模糊匹配商业公司
     */
    @MapKey("key_id")
    List<Map<String, Object>> matchCompanyByProvinceAndName(@Param("province") String province, @Param("namePattern") String namePattern);

    /**
     * 根据省份和名称模糊匹配商业公司（精确匹配，拆分后的名称）
     */
    @MapKey("key_id")
    List<Map<String, Object>> matchCompanyByProvinceAndSplitName(@Param("province") String province, @Param("namePattern") String namePattern);
}
