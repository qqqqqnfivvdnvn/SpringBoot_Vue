package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HrMonitoringDataConditionDTO;
import com.example.demo.entity.HrMonitoringData;
import com.example.demo.mapper.HrMonitoringDataMapper;
import com.example.demo.service.HrMonitoringDataService;
import com.example.demo.utils.HrToExcel;
import com.example.demo.vo.HrMonitoringDataVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.utils.MyBatisUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 恒瑞数据汇总 Service 实现
 */
@Service
public class HrMonitoringDataServiceImpl implements HrMonitoringDataService {

    @Autowired
    private HrMonitoringDataMapper monitoringDataMapper;

    @Override
    public ApiResponseDTO<PageInfo<HrMonitoringDataVO>> getDataList(HrMonitoringDataConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HrMonitoringData> dataList = monitoringDataMapper.selectByCondition(condition);

            // 使用 PageHelper 返回的 Page 对象创建 PageInfo，保留分页信息
            PageInfo<HrMonitoringData> pageInfo = new PageInfo<>(dataList);

            // 转换 VO 列表
            List<HrMonitoringDataVO> voList = new ArrayList<>();
            for (HrMonitoringData entity : dataList) {
                HrMonitoringDataVO vo = new HrMonitoringDataVO();
                BeanUtils.copyProperties(entity, vo);
                voList.add(vo);
            }

            // 创建 VO 的 PageInfo，保留原始分页信息
            PageInfo<HrMonitoringDataVO> voPageInfo = new PageInfo<>();
            BeanUtils.copyProperties(pageInfo, voPageInfo);
            voPageInfo.setList(voList);

            return ApiResponseDTO.success(voPageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportDataExcel(HrMonitoringDataConditionDTO condition) {
        try {
            // 判断是否需要限制 100 条（所有查询条件为空时）
            boolean needLimit = MyBatisUtils.isAllBlank(condition);

            List<HrMonitoringData> dataList = monitoringDataMapper.selectByConditionWithLimit(condition, needLimit);

            if (dataList == null || dataList.isEmpty()) {
                return ApiResponseDTO.error("没有数据可导出");
            }

            List<HrMonitoringDataVO> voList = new ArrayList<>();
            for (HrMonitoringData entity : dataList) {
                HrMonitoringDataVO vo = new HrMonitoringDataVO();
                BeanUtils.copyProperties(entity, vo);
                voList.add(vo);
            }

            HrToExcel hrToExcel = new HrToExcel();
            byte[] excelBytes = hrToExcel.exportMonitoringDataExcel(voList, "恒瑞数据汇总");

            return ApiResponseDTO.success(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }
}
