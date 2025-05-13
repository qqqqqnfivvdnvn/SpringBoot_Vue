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
        PieDataVO mainPieDataHosClean = homeDataMapper.getMainPieDataHosClean();
        PieDataVO mainPieDataDrugStore = homeDataMapper.getMainPieDataDrugStore();
        PieDataVO mainPieDataCompany = homeDataMapper.getMainPieDataCompany();
        List<PieDataVO> mainPieData = List.of(mainPieDataHosClean, mainPieDataDrugStore, mainPieDataCompany);
        return ApiResponseDTO.success(mainPieData);


    }

    @Override
    public ApiResponseDTO<List<PieDataVO>> getBranchBarData() {
        PieDataVO branchBarDataGeneralBranch = homeDataMapper.getBranchBarDataGeneralBranch();
        PieDataVO branchBarDataMainBranch = homeDataMapper.getBranchBarDataMainBranch();
        List<PieDataVO> branchBarData = List.of(branchBarDataGeneralBranch, branchBarDataMainBranch);
        return ApiResponseDTO.success(branchBarData);

    }

    @Override
    public ApiResponseDTO<List<AppealUpdateDataVO>> getAppealUpdateData() {
        return ApiResponseDTO.success(homeDataMapper.getAppealUpdateData());
    }


}
