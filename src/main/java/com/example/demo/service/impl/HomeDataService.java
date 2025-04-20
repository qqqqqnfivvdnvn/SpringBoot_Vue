package com.example.demo.service.impl;


import com.example.demo.dto.ApiResponse;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import com.example.demo.vo.PieData;

import java.util.List;
import java.util.Map;

public interface HomeDataService {
    public ApiResponse<DetailsData> getDataDetails();

    public ApiResponse<Map<String, Object>> getBarData();

    public ApiResponse<List<PieData>> getPieData();

    public ApiResponse<List<PieData>> getRosePieData();

}
