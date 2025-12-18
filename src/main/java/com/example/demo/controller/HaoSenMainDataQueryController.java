package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.HaoSenDrugStore;
import com.example.demo.entity.HaoSenHospital;
import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.service.impl.HaoSenMainDataQueryService;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.github.pagehelper.PageInfo;
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
import com.example.demo.entity.HaoSenCompany;

@Controller

@RequestMapping("/mainData")

public class HaoSenMainDataQueryController {

    @Autowired
    private HaoSenMainDataQueryService mainDataQueryService;


    @GetMapping("/getHospitalData")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenOrganization>>> getHospital(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            HaoSenHospitalConditionDTO hospitalCondition) {

        ApiResponseDTO<PageInfo<HaoSenOrganization>> result =
                mainDataQueryService.getHospitalList(hospitalCondition, pageNum, pageSize);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/getDrugStoreData")
    public ResponseEntity<ApiResponseDTO<Page<HaoSenDrugStore>>> getDrugStore(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @ModelAttribute HaoSenDrugStoreConditionDTO drugStoreCondition) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponseDTO<Page<HaoSenDrugStore>> drugStoreList = mainDataQueryService.getDrugStoreList(drugStoreCondition, pageable);
        return ResponseEntity.ok(drugStoreList);
    }

    @GetMapping("/getCompanyData")
    public ResponseEntity<ApiResponseDTO<Page<HaoSenCompany>>> getCompany(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @ModelAttribute HaoSenCompanyConditionDTO companyCondition) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponseDTO<Page<HaoSenCompany>> companyList = mainDataQueryService.getCompanyList(companyCondition, pageable);
        return ResponseEntity.ok(companyList);

    }

    //    医疗机构数据导出接口
    @GetMapping("/exportHospitalData")
    public ResponseEntity<ApiResponseDTO<byte[]>> exportHospitalData(@ModelAttribute HaoSenHospitalConditionDTO hospitalCondition) {


        return ResponseEntity.ok(mainDataQueryService.exportHospitalList(hospitalCondition));
    }

}
