package com.example.demo.mapper;

import com.example.demo.dto.MdLocationConditionDTO;
import com.example.demo.entity.MdLocation;
import com.example.demo.vo.MdLocationExportVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 主数据地理位置 Mapper
 */
public interface MdLocationMapper {

    /**
     * 根据条件查询地理位置列表
     */
    List<MdLocationExportVO> selectByCondition(@Param("condition") MdLocationConditionDTO condition);

    /**
     * 批量插入地理位置数据
     */
    int batchInsert(@Param("list") List<MdLocation> list);

    /**
     * 根据批次 ID 删除数据
     */
    int deleteByBatchId(@Param("batchId") String batchId);

    /**
     * 根据批次 ID 查询数据条数
     */
    int countByBatchId(@Param("batchId") String batchId);

    /**
     * 导出地理位置数据
     */
    List<MdLocationExportVO> selectExportData(@Param("condition") MdLocationConditionDTO condition);
}
