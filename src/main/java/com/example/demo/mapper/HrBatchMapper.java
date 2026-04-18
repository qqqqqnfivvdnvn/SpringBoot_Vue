package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HrBatchConditionDTO;
import com.example.demo.entity.HrBatch;
import com.example.demo.vo.HrBatchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 恒瑞批次 Mapper
 */
@Mapper
@DS("master_sqlserver")
public interface HrBatchMapper {

    List<HrBatchVO> selectByCondition(@Param("condition") HrBatchConditionDTO condition);

    HrBatchVO selectByBatchId(@Param("batchId") String batchId);

    int insert(HrBatch batch);

    int updateStatus(@Param("batchId") String batchId, @Param("status") Integer status);

    int updateStatusWithMessage(@Param("batchId") String batchId, @Param("status") Integer status, @Param("message") String message);

    int updateFileCount(@Param("batchId") String batchId, @Param("fileCount") int fileCount);
}
