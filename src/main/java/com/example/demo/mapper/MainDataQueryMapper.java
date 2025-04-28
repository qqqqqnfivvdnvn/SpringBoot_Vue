package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.Company;
import com.example.demo.entity.DrugStore;
import com.example.demo.entity.Hospital;
import com.example.demo.vo.CompanyConditionVO;
import com.example.demo.vo.DrugStoreConditionVO;
import com.example.demo.vo.HospitalConditionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
@DS("slave_pg")

public interface MainDataQueryMapper {
    public List<Hospital> HospitalQueryByCondition(@Param("condition") HospitalConditionVO condition,
                                                   @Param("page") Pageable pageable);

    long countHospitalCondition(@Param("condition") HospitalConditionVO condition);


    public List<DrugStore> DrugStoreQueryByCondition(@Param("condition") DrugStoreConditionVO condition,
                                                     @Param("page") Pageable pageable);


    long countDrugStoreCondition(@Param("condition") DrugStoreConditionVO condition);


    public List<Company> CompanyQueryByCondition(@Param("condition") CompanyConditionVO condition,
                                                   @Param("page") Pageable pageable);


    long countCompanyCondition(@Param("condition") CompanyConditionVO condition);



}
