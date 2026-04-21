package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdFuzzyMatchBatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchFileMessageDTO;
import com.example.demo.entity.MdFuzzyMatchBatch;
import com.example.demo.entity.MdFuzzyMatchSummary;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 主数据模糊匹配 Service 接口
 */
public interface MdFuzzyMatchService {

    /**
     * 获取批次列表
     */
    ApiResponseDTO<PageInfo<MdFuzzyMatchBatch>> getBatchList(MdFuzzyMatchBatchConditionDTO condition, int pageNum, int pageSize);

    /**
     * 上传文件进行模糊匹配
     */
    ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadFile(MultipartFile file, String dataType);

    /**
     * 上传文件进行模糊匹配 - 医院专用
     */
    ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadHospitalFile(MultipartFile file);

    /**
     * 上传文件进行模糊匹配 - 药店专用
     */
    ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadDrugStoreFile(MultipartFile file);

    /**
     * 上传文件进行模糊匹配 - 机构专用（不区分类型）
     */
    ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadOrganizationFile(MultipartFile file);

    /**
     * 获取汇总数据列表
     */
    ApiResponseDTO<PageInfo<MdFuzzyMatchSummary>> getSummaryList(MdFuzzyMatchConditionDTO condition, int pageNum, int pageSize);

    /**
     * 导出批次数据
     */
    ApiResponseDTO<byte[]> exportBatch(String batchId);

    /**
     * 导出汇总数据
     */
    ApiResponseDTO<byte[]> exportSummary(MdFuzzyMatchConditionDTO condition);
}
