package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdLocationConditionDTO;
import com.example.demo.mapper.MdLocationMapper;
import com.example.demo.service.MdLocationService;
import com.example.demo.utils.MdLocationToExcel;
import com.example.demo.vo.MdLocationExportVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 主数据地理位置 Service 实现
 */
@Service
public class MdLocationServiceImpl implements MdLocationService {

    @Autowired
    private MdLocationMapper locationMapper;

    @Override
    public ApiResponseDTO<PageInfo<MdLocationExportVO>> getDataList(MdLocationConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            var dataList = locationMapper.selectByCondition(condition);
            PageInfo<MdLocationExportVO> pageInfo = new PageInfo<>(dataList);
            return ApiResponseDTO.success(pageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportDataExcel(MdLocationConditionDTO condition) {
        try {
            // 查询导出数据
            List<MdLocationExportVO> dataList = locationMapper.selectExportData(condition);

            if (dataList == null || dataList.isEmpty()) {
                return ApiResponseDTO.error("没有数据可导出");
            }

            // 使用工具类导出 Excel
            MdLocationToExcel mdLocationToExcel = new MdLocationToExcel();
            byte[] excelBytes = mdLocationToExcel.exportLocationExcel(dataList, "区县经纬度获取");

            return ApiResponseDTO.success(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }
}
