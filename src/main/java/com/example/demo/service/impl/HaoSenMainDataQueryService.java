package com.example.demo.service.impl;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.HaoSenCompany;
import com.example.demo.entity.HaoSenDrugStore;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.entity.HaoSenOrganization;
import org.springframework.data.domain.Page;
import com.example.demo.entity.HaoSenHospital;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import org.springframework.data.domain.Pageable;

public interface HaoSenMainDataQueryService {

    public ApiResponseDTO<Page<HaoSenOrganization>> getHospitalList(HaoSenHospitalConditionDTO condition, Pageable pageable);

    public ApiResponseDTO<Page<HaoSenDrugStore>> getDrugStoreList(HaoSenDrugStoreConditionDTO condition, Pageable pageable);

    public ApiResponseDTO<Page<HaoSenCompany>> getCompanyList(HaoSenCompanyConditionDTO condition, Pageable pageable);


    public ApiResponseDTO<byte[]> exportHospitalList(HaoSenHospitalConditionDTO condition);


}
