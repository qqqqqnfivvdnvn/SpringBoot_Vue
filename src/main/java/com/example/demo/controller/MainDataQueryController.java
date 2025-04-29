package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.DrugStore;
import com.example.demo.entity.Hospital;
import com.example.demo.service.impl.MainDataQueryService;
import com.example.demo.dto.DrugStoreConditionDTO;
import com.example.demo.dto.HospitalConditionDTO;
import com.example.demo.dto.CompanyConditionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entity.Company;

@Controller

@RequestMapping("/mainData")

public class MainDataQueryController {

    @Autowired
    private MainDataQueryService mainDataQueryService;


    @GetMapping("/getHospitalData")
    public ResponseEntity<ApiResponseDTO<Page<Hospital>>> getHospital(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @ModelAttribute HospitalConditionDTO hospitalCondition) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponseDTO<Page<Hospital>> hospitalList = mainDataQueryService.getHospitalList(hospitalCondition, pageable);
        return  ResponseEntity.ok(hospitalList);
    }

    @GetMapping("/getDrugStoreData")
    public ResponseEntity<ApiResponseDTO<Page<DrugStore>>> getDrugStore(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @ModelAttribute DrugStoreConditionDTO drugStoreCondition) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponseDTO<Page<DrugStore>> drugStoreList = mainDataQueryService.getDrugStoreList(drugStoreCondition, pageable);
        return ResponseEntity.ok(drugStoreList);
    }

    @GetMapping("/getCompanyData")
    public ResponseEntity<ApiResponseDTO<Page<Company>>> getCompany(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @ModelAttribute CompanyConditionDTO companyCondition) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponseDTO<Page<Company>> companyList = mainDataQueryService.getCompanyList(companyCondition, pageable);
        return ResponseEntity.ok(companyList);

    }
}
