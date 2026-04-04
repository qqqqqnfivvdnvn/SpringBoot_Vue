package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenAppealConditionDTO;

import com.example.demo.vo.HaoSenOrganizationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS("slave_pg")
public interface HaoSenAppealDataMapper {

    public List<HaoSenOrganizationVO> getAppealData(@Param("condition") HaoSenAppealConditionDTO condition);


}
