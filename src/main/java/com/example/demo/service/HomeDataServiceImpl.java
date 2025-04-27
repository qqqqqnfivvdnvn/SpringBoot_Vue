package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.mapper.HomeDataMapper;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.AppealUpdateData;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import com.example.demo.vo.PieData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
    public ApiResponse<List<BarData>> getCleanBarData() {
        return ApiResponse.success(homeDataMapper.getCleanBarData());
    }

    @Override
    public ApiResponse<List<PieData>> getMainPieData() {

        return ApiResponse.success(homeDataMapper.getMainPieData());

    }

    @Override
    public ApiResponse<List<PieData>> getBranchBarData() {

        return ApiResponse.success(homeDataMapper.getBranchBarData());

    }

    @Override
    public ApiResponse<List<AppealUpdateData>> getAppealUpdateData() {
        return ApiResponse.success(homeDataMapper.getAppealUpdateData());
    }


}
