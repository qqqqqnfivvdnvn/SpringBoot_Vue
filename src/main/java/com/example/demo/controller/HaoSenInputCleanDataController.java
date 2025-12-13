package com.example.demo.controller;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.service.impl.HaoSenInputCleanDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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



}


