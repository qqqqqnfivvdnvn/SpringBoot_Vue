package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.HaoSenInputCleanDataVO;
import org.apache.ibatis.annotations.Mapper;

@DS("master_sqlserver")
@Mapper

public interface HaoSenCleanDataMapper {

    public int inputCleanData(HaoSenInputCleanDataVO inputCleanDataVO);

    public int deleteAllCleanData();

}
