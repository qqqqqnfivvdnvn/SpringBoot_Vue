package com.example.demo.service.impl;


import com.example.demo.dto.ApiResponse;
import com.example.demo.vo.AppealUpdateData;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import com.example.demo.vo.PieData;

import java.util.List;
import java.util.Map;

public interface HomeDataService {
    public ApiResponse<DetailsData> getDataDetails();

    public ApiResponse<List<BarData>> getCleanBarData();

    public ApiResponse<List<PieData>> getMainPieData();

    public ApiResponse<List<PieData>> getBranchBarData();

    public ApiResponse<List<AppealUpdateData>> getAppealUpdateData();
}
