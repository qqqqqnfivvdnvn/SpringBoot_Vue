package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Company;
import com.example.demo.entity.DrugStore;
import com.example.demo.entity.Hospital;
import com.example.demo.mapper.MainDataQueryMapper;
import com.example.demo.service.impl.MainDataQueryService;
import com.example.demo.dto.CompanyConditionDTO;
import com.example.demo.dto.DrugStoreConditionDTO;
import com.example.demo.dto.HospitalConditionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainDataQueryServiceImpl implements MainDataQueryService {

    @Autowired
    private MainDataQueryMapper mainDataQueryMapper;

    @Override
    public ApiResponseDTO<Page<Hospital>> getHospitalList(HospitalConditionDTO condition, Pageable pageable) {
        List<Hospital> hospitals = mainDataQueryMapper.HospitalQueryByCondition(condition, pageable);
        long total = mainDataQueryMapper.countHospitalCondition(condition);
        Page<Hospital> page = new PageImpl<>(hospitals, pageable, total);
        return ApiResponseDTO.success(page);
    }

    @Override
    public ApiResponseDTO<Page<DrugStore>> getDrugStoreList(DrugStoreConditionDTO condition, Pageable pageable) {
        List<DrugStore> drugStores = mainDataQueryMapper.DrugStoreQueryByCondition(condition, pageable);
        long total = mainDataQueryMapper.countDrugStoreCondition(condition);
        Page<DrugStore> page = new PageImpl<>(drugStores, pageable, total);
        return ApiResponseDTO.success(page);
    }

    @Override
    public ApiResponseDTO<Page<Company>> getCompanyList(CompanyConditionDTO condition, Pageable pageable) {
        List<Company> companies = mainDataQueryMapper.CompanyQueryByCondition(condition,pageable);
        long total = mainDataQueryMapper.countCompanyCondition(condition);
        Page<Company> page = new PageImpl<>(companies, pageable, total);
        return ApiResponseDTO.success(page);
    }


}
