package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.mapper.HaoSenHomeDataMapper;
import com.example.demo.service.HaoSenHomeDataService;
import com.example.demo.vo.HaoSenAppealUpdateDataVO;
import com.example.demo.vo.HaoSenBarDataVO;
import com.example.demo.vo.HaoSenDetailsDataVO;
import com.example.demo.vo.PieDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HaoSenHomeDataServiceImpl implements HaoSenHomeDataService {
    @Autowired
    private HaoSenHomeDataMapper homeDataMapper;



    @Override
    public ApiResponseDTO<HaoSenDetailsDataVO> getDataDetails() {
        try {
            HaoSenDetailsDataVO dataDetails = homeDataMapper.getHomeData();
            return ApiResponseDTO.success(dataDetails);
        } catch (Exception e) {
            return ApiResponseDTO.error("数据获取失败");
        }
    }

    @Override
    public ApiResponseDTO<List<HaoSenBarDataVO>> getCleanBarData() {
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
    public ApiResponseDTO<List<HaoSenAppealUpdateDataVO>> getAppealUpdateData() {
        return ApiResponseDTO.success(homeDataMapper.getAppealUpdateData());
    }


}
