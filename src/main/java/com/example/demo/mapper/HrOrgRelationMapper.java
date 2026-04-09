package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HrOrgRelationConditionDTO;
import com.example.demo.entity.HrOrgRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 恒瑞数据比对关系 Mapper
 */
@Mapper
@DS("master_sqlserver")
public interface HrOrgRelationMapper {

    List<HrOrgRelation> selectByCondition(@Param("condition") HrOrgRelationConditionDTO condition);

    int insert(HrOrgRelation relation);

    int update(HrOrgRelation relation);

    int deleteById(@Param("businessLicenseName") String businessLicenseName);

    int syncFromTemp(@Param("batchId") String batchId);
}
