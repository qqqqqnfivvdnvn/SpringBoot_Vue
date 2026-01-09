package com.example.demo.controller;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenCleanConditionDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.entity.HaoSenCleanData;
import com.example.demo.service.HaoSenInputCleanDataService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller

@RequestMapping("/cleanData")
public class HaoSenInputCleanDataController {

	@Autowired
	private HaoSenInputCleanDataService inputCleanDataService;


	//    导入清洗数据
	@PostMapping("/importCleanData")
	public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> importCleanData(@RequestParam("file") MultipartFile file) {
		ApiResponseDTO<HaoSenFileMessageDTO> haoSenFileMessageDTOApiResponseDTO = inputCleanDataService.uploadUpdateFile(file);

		return ResponseEntity.ok(haoSenFileMessageDTOApiResponseDTO);

	}



	@PostMapping("/selectCleanData")
	public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenCleanData>>> selectCleanData(@RequestParam(defaultValue = "1") int pageNum,
																					 @RequestParam(defaultValue = "20") int pageSize,
																					 @ModelAttribute HaoSenCleanConditionDTO CleanCondition) {
		ApiResponseDTO<PageInfo<HaoSenCleanData>> pageInfoApiResponseDTO = inputCleanDataService.selectHaoSenCleanData(CleanCondition, pageNum, pageSize);
		return ResponseEntity.ok(pageInfoApiResponseDTO);
	}



}


