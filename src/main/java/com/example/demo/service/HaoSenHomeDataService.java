package com.example.demo.service;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.vo.HaoSenAppealUpdateDataVO;
import com.example.demo.vo.HaoSenBarDataVO;
import com.example.demo.vo.HaoSenDetailsDataVO;
import com.example.demo.vo.PieDataVO;

import java.util.List;

public interface HaoSenHomeDataService {
    public ApiResponseDTO<HaoSenDetailsDataVO> getDataDetails();

    public ApiResponseDTO<List<HaoSenBarDataVO>> getCleanBarData();

    public ApiResponseDTO<List<PieDataVO>> getMainPieData();

    public ApiResponseDTO<List<PieDataVO>> getBranchBarData();

    public ApiResponseDTO<List<HaoSenAppealUpdateDataVO>> getAppealUpdateData();
}
