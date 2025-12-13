package com.example.demo.service.impl;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenAppealConditionDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.vo.HaoSenAppealDataVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HaoSenAppealDataService {

    public ApiResponseDTO<Page<HaoSenAppealDataVO>> getAppealData(HaoSenAppealConditionDTO condition, Pageable pageable);


    public ApiResponseDTO<byte[]> exportAppealDataExcel();


    public ApiResponseDTO<HaoSenFileMessageDTO> uploadAppealFile(MultipartFile file);


    public ApiResponseDTO<HaoSenFileMessageDTO> handleAppealData(HaoSenAppealDataVO haoSenAppealDataVO );




}
