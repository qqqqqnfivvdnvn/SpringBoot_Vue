package com.example.demo.mapper;

import com.example.demo.dto.MdLocationBatchConditionDTO;
import com.example.demo.entity.MdLocationBatch;
import com.example.demo.vo.MdLocationBatchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 主数据地理位置批次 Mapper
 */
public interface MdLocationBatchMapper {

    /**
     * 根据条件查询批次列表
     */
    List<MdLocationBatchVO> selectByCondition(@Param("condition") MdLocationBatchConditionDTO condition);

    /**
     * 插入批次记录
     */
    int insert(MdLocationBatch batch);

    /**
     * 根据批次 ID 查询批次
     */
    MdLocationBatch selectByBatchId(@Param("batchId") String batchId);

    /**
     * 更新批次状态
     */
    int updateStatus(@Param("batchId") String batchId,
                     @Param("status") Integer status,
                     @Param("message") String message);

    /**
     * 根据批次 ID 删除批次
     */
    int deleteByBatchId(@Param("batchId") String batchId);
}
