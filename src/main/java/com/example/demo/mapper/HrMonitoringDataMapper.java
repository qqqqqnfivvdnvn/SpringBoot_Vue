package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HrMonitoringDataConditionDTO;
import com.example.demo.entity.HrMonitoringData;
import com.example.demo.vo.HrBatchExportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 恒瑞数据汇总 Mapper
 */
@Mapper
@DS("master_sqlserver")
public interface HrMonitoringDataMapper {

    List<HrMonitoringData> selectByCondition(@Param("condition") HrMonitoringDataConditionDTO condition);

    List<HrMonitoringData> selectByConditionWithLimit(@Param("condition") HrMonitoringDataConditionDTO condition, @Param("needLimit") boolean needLimit);

    int batchInsert(@Param("list") List<HrMonitoringData> list);

    int deleteByBatchId(@Param("batchId") String batchId);

    int deleteAllTemp();

    void transferFromTemp(@Param("batchId") String batchId);

    List<HrMonitoringData> selectByBatchId(@Param("batchId") String batchId);

    List<HrBatchExportVO> selectExportDataByBatchId(@Param("batchId") String batchId);
}
