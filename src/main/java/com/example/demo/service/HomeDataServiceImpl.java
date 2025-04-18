package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.mapper.HomeDataMapper;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HomeDataServiceImpl implements HomeDataService {
    @Autowired
    private HomeDataMapper homeDataMapper;



    @Override
    public ApiResponse<DetailsData> getDataDetails() {
        try {
            DetailsData dataDetails = homeDataMapper.getHomeData();
            return ApiResponse.success(dataDetails);
        } catch (Exception e) {
            return ApiResponse.error("数据获取失败");
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> getBarData() {
        List<BarData> barDataList = homeDataMapper.getBarData();
        Map<String, Object> result = new HashMap<>();

        List<String> dates = barDataList.stream()
                .map(BarData::getDay)
                .toList();

        List<Integer> hospital = barDataList.stream()
                .map(BarData::getHosCount)
                .toList();

        List<Integer> drugstore = barDataList.stream()
                .map(BarData::getDrugstoreCount)
                .toList();

        List<Integer> company = barDataList.stream()
                .map(BarData::getCompanyCount)
                .toList();

        result.put("dates", dates);
        result.put("hospital", hospital);
        result.put("drugstore", drugstore);
        result.put("company", company);

        return ApiResponse.success(result);
    }
}
