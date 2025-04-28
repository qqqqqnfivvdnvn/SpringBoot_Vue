package com.example.demo.service.impl;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.vo.AppealUpdateDataVO;
import com.example.demo.vo.BarDataVO;
import com.example.demo.vo.DetailsDataVO;
import com.example.demo.vo.PieDataVO;

import java.util.List;

public interface HomeDataService {
    public ApiResponseDTO<DetailsDataVO> getDataDetails();

    public ApiResponseDTO<List<BarDataVO>> getCleanBarData();

    public ApiResponseDTO<List<PieDataVO>> getMainPieData();

    public ApiResponseDTO<List<PieDataVO>> getBranchBarData();

    public ApiResponseDTO<List<AppealUpdateDataVO>> getAppealUpdateData();
}
