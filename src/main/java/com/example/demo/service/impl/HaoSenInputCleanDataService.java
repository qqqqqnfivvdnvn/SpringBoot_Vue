package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface HaoSenInputCleanDataService {


    public ApiResponseDTO<HaoSenFileMessageDTO> uploadUpdateFile(MultipartFile file);





}
