package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.Company;
import com.example.demo.entity.DrugStore;
import com.example.demo.entity.Hospital;
import com.example.demo.dto.CompanyConditionDTO;
import com.example.demo.dto.DrugStoreConditionDTO;
import com.example.demo.dto.HospitalConditionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
@DS("slave_pg")

public interface MainDataQueryMapper {
    public List<Hospital> HospitalQueryByCondition(@Param("condition") HospitalConditionDTO condition,
                                                   @Param("page") Pageable pageable);

    long countHospitalCondition(@Param("condition") HospitalConditionDTO condition);


    public List<DrugStore> DrugStoreQueryByCondition(@Param("condition") DrugStoreConditionDTO condition,
                                                     @Param("page") Pageable pageable);


    long countDrugStoreCondition(@Param("condition") DrugStoreConditionDTO condition);


    public List<Company> CompanyQueryByCondition(@Param("condition") CompanyConditionDTO condition,
                                                   @Param("page") Pageable pageable);


    long countCompanyCondition(@Param("condition") CompanyConditionDTO condition);



}
