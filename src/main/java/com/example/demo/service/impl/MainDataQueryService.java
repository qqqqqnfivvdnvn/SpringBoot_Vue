package com.example.demo.service.impl;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Company;
import com.example.demo.entity.DrugStore;
import com.example.demo.dto.CompanyConditionDTO;
import com.example.demo.dto.DrugStoreConditionDTO;
import org.springframework.data.domain.Page;
import com.example.demo.entity.Hospital;
import com.example.demo.dto.HospitalConditionDTO;
import org.springframework.data.domain.Pageable;

public interface MainDataQueryService {

    public ApiResponseDTO<Page<Hospital>> getHospitalList(HospitalConditionDTO condition, Pageable pageable);

    public ApiResponseDTO<Page<DrugStore>> getDrugStoreList(DrugStoreConditionDTO condition, Pageable pageable);

    public ApiResponseDTO<Page<Company>> getCompanyList(CompanyConditionDTO condition, Pageable pageable);

}
