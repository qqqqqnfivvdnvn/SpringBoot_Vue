package com.example.demo.controller;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.dto.HaoSenUpdateStatusDTO;
import com.example.demo.vo.HaoSenOrganizationVO;
import com.example.demo.service.HaoSenUpdateDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Controller
@RequestMapping("/haosen/updatedata")
public class HaoSenInputUpdateDataController {

    //    导入更新数据
    @Autowired
    private HaoSenUpdateDataService haoSenUpdateDataService;

    @PostMapping("/importupdatedata")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> importAppealData(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(haoSenUpdateDataService.uploadUpdateFile(file));


    }


    //    单条页面查询医院、药店大库数据
    @GetMapping("/finddakupdata")
    public ResponseEntity<ApiResponseDTO<HaoSenOrganizationVO>> findDaKuData(@RequestParam("keyid") String keyid)  {
        return ResponseEntity.ok(haoSenUpdateDataService.findDaKuData(keyid));

    }



    //单条页面更新医院、药店、商业 数据，推送接口更新数据
    @PostMapping("/oneupdatehaosendata")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> OneUpdateHaoSenData(@RequestBody HaoSenOrganizationVO haoSenOrganization ) {

        return ResponseEntity.ok(haoSenUpdateDataService.updateOneUpdateData(haoSenOrganization));


    }




    //更新医院、药店、商业的状态 status 相关字段 status  1 正常,2 作废,3 无法清洗,4 豪森禁用客户,5 重复数据
    @PostMapping("/updateinstitutionstatus")
    public ResponseEntity<ApiResponseDTO<Integer>> updateInstitutionStatus(@RequestBody HaoSenUpdateStatusDTO haoSenUpdateStatusDTO ) {

        return ResponseEntity.ok(haoSenUpdateDataService.updateInstitutionType(haoSenUpdateStatusDTO));


    }


    @PostMapping("/deleteinstitutiondata")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteInstitutionData( @RequestBody Map<String, String> params) {
        return ResponseEntity.ok(haoSenUpdateDataService.deleteInstitutionData(params));
    }












}

    

