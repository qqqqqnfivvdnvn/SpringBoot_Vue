package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenAppealConditionDTO;

import com.example.demo.vo.HaoSenAppealDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
@DS("slave_pg")
public interface HaoSenAppealDataMapper {
    public List<HaoSenAppealDataVO> getAppealData(@Param("condition") HaoSenAppealConditionDTO condition,
                                                  @Param("page") Pageable pageable);


    long countAppealCondition(@Param("condition") HaoSenAppealConditionDTO condition);



}
