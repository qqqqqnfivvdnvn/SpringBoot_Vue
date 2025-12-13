package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.HaoSenAppealDataVO;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
@DS("master_sqlserver")
public interface HaoSenSqlMapper {

    public int inputHaoSenData(HaoSenInputAppealDataVO inputAppealDataVO) ;

    public int deleteAllHaoSenData();

    public HaoSenAppealDataVO findDaKuData(String keyid);

    public int inputHaoSenUpdateData(HaoSenAppealDataVO  updateAppealDataVO);




}
