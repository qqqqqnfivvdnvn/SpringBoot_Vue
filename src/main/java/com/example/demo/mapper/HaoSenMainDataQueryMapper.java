package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.HaoSenCompany;
import com.example.demo.entity.HaoSenDrugStore;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import com.example.demo.entity.HaoSenOrganization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
@DS("slave_pg")

public interface HaoSenMainDataQueryMapper {
    public List<HaoSenOrganization> HospitalQueryByCondition(@Param("condition") HaoSenHospitalConditionDTO condition,
                                                             @Param("page") Pageable pageable);
    long countHospitalCondition(@Param("condition") HaoSenHospitalConditionDTO condition);

    public List<HaoSenOrganization> findAllHospitalCondition(@Param("condition") HaoSenHospitalConditionDTO condition, @Param("_isEmptyCondition") boolean _isEmptyCondition);



    public List<HaoSenDrugStore> DrugStoreQueryByCondition(@Param("condition") HaoSenDrugStoreConditionDTO condition,
                                                           @Param("page") Pageable pageable);
    long countDrugStoreCondition(@Param("condition") HaoSenDrugStoreConditionDTO condition);


    public List<HaoSenCompany> CompanyQueryByCondition(@Param("condition") HaoSenCompanyConditionDTO condition,
                                                       @Param("page") Pageable pageable);
    long countCompanyCondition(@Param("condition") HaoSenCompanyConditionDTO condition);



}
