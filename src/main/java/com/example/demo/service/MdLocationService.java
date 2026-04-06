package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdLocationConditionDTO;
import com.example.demo.vo.MdLocationExportVO;
import com.github.pagehelper.PageInfo;

/**
 * 主数据地理位置 Service
 */
public interface MdLocationService {

    /**
     * 获取地理位置列表
     */
    ApiResponseDTO<PageInfo<MdLocationExportVO>> getDataList(MdLocationConditionDTO condition, int pageNum, int pageSize);

    /**
     * 导出地理位置 Excel
     */
    ApiResponseDTO<byte[]> exportDataExcel(MdLocationConditionDTO condition);
}
