package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenCleanConditionDTO;
import com.example.demo.entity.HaoSenCleanData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("slave_pg")
@Mapper

public interface HaoSenCleanDataMapper {

    public List<HaoSenCleanData> selectHaoSenCleanData(@Param("condition") HaoSenCleanConditionDTO condition);


}
