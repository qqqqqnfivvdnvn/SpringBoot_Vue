package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenCleanConditionDTO;
import com.example.demo.vo.HaoSenCleanDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("slave_pg")
@Mapper
public interface HaoSenCleanDataMapper {

    public List<HaoSenCleanDataVO> selectHaoSenCleanData(@Param("condition") HaoSenCleanConditionDTO condition);


    public List<HaoSenCleanDataVO> findAllHaoSenCleanData(@Param("condition") HaoSenCleanConditionDTO condition, @Param("needLimit") boolean needLimit);


}
