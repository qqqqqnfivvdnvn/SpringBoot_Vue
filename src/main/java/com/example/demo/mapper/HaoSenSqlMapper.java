package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.HaoSenOrganizationVO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
@DS("master_sqlserver")
public interface HaoSenSqlMapper {


    public int deleteAllHaoSenData();

    public HaoSenOrganizationVO findDaKuData(String keyid);


    public int inputHaoSenUpdateData(HaoSenOrganizationVO  HaoSenOrganizationVO);




}
