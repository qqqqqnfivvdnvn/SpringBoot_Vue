package com.example.demo.controller;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.entity.HaoSenHospital;
import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.service.impl.HaoSenUpdateDataService;
import com.example.demo.vo.HaoSenAppealDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@Controller
@RequestMapping("/updateData")
public class HaoSenInputUpdateDataController {

    //    导入更新数据
    @Autowired
    private HaoSenUpdateDataService haoSenUpdateDataService;

    @PostMapping("/importUpdateData")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> importAppealData(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(haoSenUpdateDataService.uploadUpdateFile(file));



    }




    //单条页面更新医院数据，推送接口更新数据
    @PostMapping("/inputOneUpdateHospitalData")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> inputOneAppealData(@RequestBody HaoSenOrganization haoSenOrganization ) {


        return ResponseEntity.ok(haoSenUpdateDataService.updateHospitalData(haoSenOrganization));

    }



    //    单条页面查询医院、药店大库数据
    @GetMapping ("/findDaKuData")
    public ResponseEntity<ApiResponseDTO<HaoSenAppealDataVO>> findDaKuData(@RequestParam("keyid") String keyid)  {
        return ResponseEntity.ok(haoSenUpdateDataService.findDaKuData(keyid));

    }





}

    

