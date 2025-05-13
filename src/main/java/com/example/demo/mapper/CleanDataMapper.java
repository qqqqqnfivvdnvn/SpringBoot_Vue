package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.InputCleanDataVO;
import org.apache.ibatis.annotations.Mapper;

@DS("master_sqlserver")
@Mapper

public interface CleanDataMapper {

    public int inputCleanData(InputCleanDataVO inputCleanDataVO);

    public int deleteAllCleanData();

}
