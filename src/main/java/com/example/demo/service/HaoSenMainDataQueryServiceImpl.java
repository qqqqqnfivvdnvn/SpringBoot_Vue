package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.HaoSenCompany;
import com.example.demo.entity.HaoSenDrugStore;
import com.example.demo.entity.HaoSenHospital;
import com.example.demo.mapper.HaoSenMainDataQueryMapper;
import com.example.demo.service.impl.HaoSenMainDataQueryService;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HaoSenMainDataQueryServiceImpl implements HaoSenMainDataQueryService {

    @Autowired
    private HaoSenMainDataQueryMapper mainDataQueryMapper;

    @Override
    public ApiResponseDTO<Page<HaoSenHospital>> getHospitalList(HaoSenHospitalConditionDTO condition, Pageable pageable) {
        List<HaoSenHospital> hospitals = mainDataQueryMapper.HospitalQueryByCondition(condition, pageable);
        long total = mainDataQueryMapper.countHospitalCondition(condition);
        Page<HaoSenHospital> page = new PageImpl<>(hospitals, pageable, total);
        return ApiResponseDTO.success(page);
    }

    @Override
    public ApiResponseDTO<Page<HaoSenDrugStore>> getDrugStoreList(HaoSenDrugStoreConditionDTO condition, Pageable pageable) {
        List<HaoSenDrugStore> drugStores = mainDataQueryMapper.DrugStoreQueryByCondition(condition, pageable);
        long total = mainDataQueryMapper.countDrugStoreCondition(condition);
        Page<HaoSenDrugStore> page = new PageImpl<>(drugStores, pageable, total);
        return ApiResponseDTO.success(page);
    }

    @Override
    public ApiResponseDTO<Page<HaoSenCompany>> getCompanyList(HaoSenCompanyConditionDTO condition, Pageable pageable) {
        List<HaoSenCompany> companies = mainDataQueryMapper.CompanyQueryByCondition(condition,pageable);
        long total = mainDataQueryMapper.countCompanyCondition(condition);
        Page<HaoSenCompany> page = new PageImpl<>(companies, pageable, total);
        return ApiResponseDTO.success(page);
    }


}
