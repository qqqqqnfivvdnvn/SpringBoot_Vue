package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import com.example.demo.vo.HaoSenOrganizationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS("slave_pg")

public interface HaoSenMainDataQueryMapper  {
    public List<HaoSenOrganizationVO> HospitalQueryByCondition(@Param("condition") HaoSenHospitalConditionDTO condition);

    public List<HaoSenOrganizationVO> findAllHospitalCondition(@Param("condition") HaoSenHospitalConditionDTO condition, @Param("needLimit") boolean needLimit);


    public List<HaoSenOrganizationVO> DrugStoreQueryByCondition(@Param("condition") HaoSenDrugStoreConditionDTO condition);

    public List<HaoSenOrganizationVO> findAllDrugStoreCondition(@Param("condition") HaoSenDrugStoreConditionDTO condition, @Param("needLimit") boolean needLimit);


    public List<HaoSenOrganizationVO> CompanyQueryByCondition(@Param("condition") HaoSenCompanyConditionDTO condition);

    public List<HaoSenOrganizationVO> findAllCompanyCondition(@Param("condition") HaoSenCompanyConditionDTO condition, @Param("needLimit") boolean needLimit);



}
