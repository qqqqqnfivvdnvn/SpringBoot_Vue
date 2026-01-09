package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.service.HaoSenMainDataQueryService;
import com.example.demo.dto.HaoSenDrugStoreConditionDTO;
import com.example.demo.dto.HaoSenHospitalConditionDTO;
import com.example.demo.dto.HaoSenCompanyConditionDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    //    医疗机构数据导出接口
    @GetMapping("/exportHospitalData")
    public ResponseEntity<ApiResponseDTO<byte[]>> exportHospitalData(@ModelAttribute HaoSenHospitalConditionDTO hospitalCondition) {


        return ResponseEntity.ok(mainDataQueryService.exportHospitalList(hospitalCondition));

    }


    @GetMapping("/getDrugStoreData")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenOrganization>>> getDrugStore( @RequestParam(defaultValue = "1") int pageNum,
                                                                                      @RequestParam(defaultValue = "20") int pageSize,
                                                                                      @ModelAttribute HaoSenDrugStoreConditionDTO drugStoreCondition) {
        ApiResponseDTO<PageInfo<HaoSenOrganization>> drugStoreList = mainDataQueryService.getDrugStoreList(drugStoreCondition, pageNum,pageSize);
        return ResponseEntity.ok(drugStoreList);

    }


    @GetMapping("/exportDrugStoreData")
    public ResponseEntity<ApiResponseDTO<byte[]>> exportDrugStoreData(@ModelAttribute  HaoSenDrugStoreConditionDTO drugStoreCondition) {

        return ResponseEntity.ok(mainDataQueryService.exportDrugStoreList(drugStoreCondition));


    }


    @GetMapping("/getCompanyData")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenOrganization>>> getCompany(@RequestParam(defaultValue = "1") int pageNum,
                                                                                   @RequestParam(defaultValue = "20") int pageSize,
                                                                                   @ModelAttribute HaoSenCompanyConditionDTO companyCondition) {


        return ResponseEntity.ok(mainDataQueryService.getCompanyList(companyCondition, pageNum, pageSize));


    }


    @GetMapping("/exportCompanyData")
    public ResponseEntity<ApiResponseDTO<byte[]>> exportCompanyData(@ModelAttribute  HaoSenCompanyConditionDTO companyCondition) {

        return ResponseEntity.ok(mainDataQueryService.exportCompanyData(companyCondition));


    }







}
