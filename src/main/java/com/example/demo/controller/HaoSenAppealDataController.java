package com.example.demo.controller;

import com.example.demo.dto.HaoSenFileMessageDTO;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenAppealConditionDTO;
import com.example.demo.service.HaoSenAppealDataService;
import com.example.demo.vo.HaoSenOrganizationVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



@Controller

@RequestMapping("/haosen/appealdata")
public class HaoSenAppealDataController {

    @Autowired
    private HaoSenAppealDataService appealDataService;

    // 获取申诉数据
    @GetMapping("/getappealdata")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenOrganizationVO>>> getAppealData(@RequestParam(defaultValue = "1") int pageNum,
                                                                                        @RequestParam(defaultValue = "10") int pageSize, HaoSenAppealConditionDTO condition) {
        ApiResponseDTO<PageInfo<HaoSenOrganizationVO>> appealData = appealDataService.getAppealData(condition,pageNum,pageSize );
        return ResponseEntity.ok(appealData);
    }


    // 导出申诉数据
    @GetMapping("/exportappealdata")
    public ResponseEntity<ApiResponseDTO<byte[]>> exportAppealData(HaoSenAppealConditionDTO condition) {
        // 获取申诉数据
        ApiResponseDTO<byte[]> apiResponseDTO = appealDataService.exportAppealDataExcel(condition);
        return ResponseEntity.ok(apiResponseDTO);

    }


    // 导入申诉数据
    @PostMapping("/importappealdata")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> importAppealData(@RequestParam("file") MultipartFile file) {
        ApiResponseDTO<HaoSenFileMessageDTO> apiResponseDTO = appealDataService.uploadAppealFile(file);
        return ResponseEntity.ok(apiResponseDTO);
    }



    //页面上单条处理申诉数据 ，推送申诉数据
    @PostMapping("/handleappealdata")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> handleAppealData(@RequestBody HaoSenOrganizationVO haoSenOrganizationVO) {

        ApiResponseDTO<HaoSenFileMessageDTO> apiResponseDTO = appealDataService.handleAppealData(haoSenOrganizationVO);

        return ResponseEntity.ok(apiResponseDTO);

    }




}
