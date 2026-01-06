package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import com.example.demo.entity.HaoSenOrganization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS("slave_pg")

public interface HaoSenMainDataQueryMapper {
    public List<HaoSenOrganization> HospitalQueryByCondition(@Param("condition") HaoSenHospitalConditionDTO condition);

    public List<HaoSenOrganization> findAllHospitalCondition(@Param("condition") HaoSenHospitalConditionDTO condition, @Param("needLimit") boolean needLimit);


    public List<HaoSenOrganization> DrugStoreQueryByCondition(@Param("condition") HaoSenDrugStoreConditionDTO condition);

    public List<HaoSenOrganization> findAllDrugStoreCondition(@Param("condition") HaoSenDrugStoreConditionDTO condition, @Param("needLimit") boolean needLimit);



    public List<HaoSenOrganization> CompanyQueryByCondition(@Param("condition") HaoSenCompanyConditionDTO condition);

    public List<HaoSenOrganization> findAllCompanyCondition(@Param("condition") HaoSenCompanyConditionDTO condition, @Param("needLimit") boolean needLimit);



}
