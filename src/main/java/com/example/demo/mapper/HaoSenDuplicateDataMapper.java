package com.example.demo.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenDuplicateConditionDtO;
import com.example.demo.vo.HaoSenCheckDuplicateDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

public interface HaoSenDuplicateDataMapper {

    List<HaoSenCheckDuplicateDataVO> getDuplicateData(@Param("condition") HaoSenDuplicateConditionDtO condition);

    public Integer updateDuplicateData();

    List<HaoSenCheckDuplicateDataVO> getExportHSData(@Param("condition") HaoSenDuplicateConditionDtO condition);


}
