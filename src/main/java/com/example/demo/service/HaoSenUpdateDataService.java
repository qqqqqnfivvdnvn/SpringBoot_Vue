package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.dto.HaoSenUpdateStatusDTO;
import com.example.demo.vo.HaoSenOrganizationVO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface HaoSenUpdateDataService {


    public ApiResponseDTO<HaoSenFileMessageDTO> uploadUpdateFile(MultipartFile file);

    public ApiResponseDTO<HaoSenOrganizationVO> findDaKuData(String keyid);

    public ApiResponseDTO<HaoSenFileMessageDTO> updateOneUpdateData(HaoSenOrganizationVO haoSenOrganization);



    public ApiResponseDTO<Integer> updateInstitutionType( HaoSenUpdateStatusDTO haoSenUpdateStatusDTO);


    public ApiResponseDTO<Integer> deleteInstitutionData(Map<String, String> params);


}
