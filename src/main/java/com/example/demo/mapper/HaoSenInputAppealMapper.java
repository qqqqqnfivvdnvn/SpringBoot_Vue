package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import org.apache.ibatis.annotations.Mapper;

@DS("master_sqlserver")
@Mapper
public interface HaoSenInputAppealMapper {

    public int inputAppeal(HaoSenInputAppealDataVO inputAppealDataVO );

    public int deleteAllAppeal();
}
