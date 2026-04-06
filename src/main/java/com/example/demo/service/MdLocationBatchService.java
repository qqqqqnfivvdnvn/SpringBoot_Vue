package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdLocationBatchConditionDTO;
import com.example.demo.vo.MdLocationBatchVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 主数据地理位置批次管理 Service
 */
public interface MdLocationBatchService {

    /**
     * 获取批次列表
     */
    ApiResponseDTO<PageInfo<MdLocationBatchVO>> getBatchList(MdLocationBatchConditionDTO condition, int pageNum, int pageSize);

    /**
     * 上传批次文件
     */
    ApiResponseDTO<?> uploadBatchFile(MultipartFile file);
}
