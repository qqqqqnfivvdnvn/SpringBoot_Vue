package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.mapper.HomeDataMapper;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.AppealUpdateDataVO;
import com.example.demo.vo.BarDataVO;
import com.example.demo.vo.DetailsDataVO;
import com.example.demo.vo.PieDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HomeDataServiceImpl implements HomeDataService {
    @Autowired
    private HomeDataMapper homeDataMapper;



    @Override
    public ApiResponseDTO<DetailsDataVO> getDataDetails() {
        try {
            DetailsDataVO dataDetails = homeDataMapper.getHomeData();
            return ApiResponseDTO.success(dataDetails);
        } catch (Exception e) {
            return ApiResponseDTO.error("数据获取失败");
        }
    }

    @Override
    public ApiResponseDTO<List<BarDataVO>> getCleanBarData() {
        return ApiResponseDTO.success(homeDataMapper.getCleanBarData());
    }

    @Override
    public ApiResponseDTO<List<PieDataVO>> getMainPieData() {

        return ApiResponseDTO.success(homeDataMapper.getMainPieData());

    }

    @Override
    public ApiResponseDTO<List<PieDataVO>> getBranchBarData() {

        return ApiResponseDTO.success(homeDataMapper.getBranchBarData());

    }

    @Override
    public ApiResponseDTO<List<AppealUpdateDataVO>> getAppealUpdateData() {
        return ApiResponseDTO.success(homeDataMapper.getAppealUpdateData());
    }


}
