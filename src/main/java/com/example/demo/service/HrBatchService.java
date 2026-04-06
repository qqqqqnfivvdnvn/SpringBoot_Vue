package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HrBatchConditionDTO;
import com.example.demo.dto.HrMonitoringDataConditionDTO;
import com.example.demo.dto.HrOrgRelationConditionDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.vo.HrBatchVO;
import com.example.demo.vo.HrMonitoringDataVO;
import com.example.demo.vo.HrOrgRelationVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 恒瑞批次管理 Service
 */
public interface HrBatchService {

    ApiResponseDTO<PageInfo<HrBatchVO>> getBatchList(HrBatchConditionDTO condition, int pageNum, int pageSize);

    ApiResponseDTO<HaoSenFileMessageDTO> uploadBatchFile(MultipartFile file);

    ApiResponseDTO<byte[]> exportBatchExcel(HrBatchConditionDTO condition);
}
