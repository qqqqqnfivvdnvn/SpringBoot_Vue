package com.example.demo.service.impl;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Company;
import com.example.demo.entity.DrugStore;
import com.example.demo.vo.CompanyConditionVO;
import com.example.demo.vo.DrugStoreConditionVO;
import org.springframework.data.domain.Page;
import com.example.demo.entity.Hospital;
import com.example.demo.vo.HospitalConditionVO;
import org.springframework.data.domain.Pageable;

public interface MainDataQueryService {

    public ApiResponseDTO<Page<Hospital>> getHospitalList(HospitalConditionVO condition, Pageable pageable);

    public ApiResponseDTO<Page<DrugStore>> getDrugStoreList(DrugStoreConditionVO condition, Pageable pageable);

    public ApiResponseDTO<Page<Company>> getCompanyList(CompanyConditionVO condition, Pageable pageable);

}
