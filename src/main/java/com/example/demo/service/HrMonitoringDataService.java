package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HrMonitoringDataConditionDTO;
import com.example.demo.vo.HrMonitoringDataVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 恒瑞数据汇总 Service
 */
public interface HrMonitoringDataService {

    ApiResponseDTO<PageInfo<HrMonitoringDataVO>> getDataList(HrMonitoringDataConditionDTO condition, int pageNum, int pageSize);

    ApiResponseDTO<byte[]> exportDataExcel(HrMonitoringDataConditionDTO condition);
}
