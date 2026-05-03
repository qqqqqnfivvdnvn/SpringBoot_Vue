package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdFuzzyMatchBatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchConditionDTO;
import com.example.demo.dto.MdFuzzyMatchFileMessageDTO;
import com.example.demo.entity.MdFuzzyMatchBatch;
import com.example.demo.entity.MdFuzzyMatchSummary;
import com.example.demo.mapper.MdFuzzyMatchMapper;
import com.example.demo.service.MdFuzzyMatchService;
import com.example.demo.utils.MdFuzzyMatchToExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 主数据模糊匹配 Service 实现
 */
@Service
public class MdFuzzyMatchServiceImpl implements MdFuzzyMatchService {

    @Autowired
    private MdFuzzyMatchMapper mdFuzzyMatchMapper;

    @Value("${file.upload-dir:/tmp}/fuzzy_file")
    private String uploadDir;

    @Autowired
    private MdFuzzyMatchProcessServiceImpl processService;

    @Autowired
    private MdFuzzyMatchToExcel mdFuzzyMatchToExcel;

    @Override
    public ApiResponseDTO<PageInfo<MdFuzzyMatchBatch>> getBatchList(MdFuzzyMatchBatchConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<MdFuzzyMatchBatch> batchList = mdFuzzyMatchMapper.getBatchList(
                    condition.getBatchId(),
                    condition.getStatus(),
                    condition.getDataType(),
                    condition.getStartTime(),
                    condition.getEndTime());
            PageInfo<MdFuzzyMatchBatch> pageInfo = new PageInfo<>(batchList);

            // 为每个批次设置状态描述
            for (MdFuzzyMatchBatch batch : pageInfo.getList()) {
                batch.setStatusDesc(batch.getStatusDesc());
            }

            return ApiResponseDTO.success(pageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadFile(MultipartFile file, String dataType) {
        MdFuzzyMatchFileMessageDTO fileMessage = new MdFuzzyMatchFileMessageDTO();

        try {
            // 1. 保存文件到服务器
            String originalFilename = file.getOriginalFilename();
            String batchId = UUID.randomUUID().toString();
            String filePath = saveFile(file, batchId);

            // 2. 创建批次记录
            MdFuzzyMatchBatch batch = new MdFuzzyMatchBatch();
            batch.setBatchId(batchId);
            batch.setDataType(dataType); // 设置数据类型
            batch.setStatus(0); // 处理中
            batch.setOriginalFilename(originalFilename);
            batch.setMessage("文件已上传，开始处理...");
            batch.setCreateTime(LocalDateTime.now());
            batch.setUpdateTime(LocalDateTime.now());

            mdFuzzyMatchMapper.insertBatch(batch);

            fileMessage.setBatchId(batchId);
            fileMessage.setMessage("文件上传成功，批次 ID: " + batchId);

            // 3. 异步处理文件（直接调用 @Async 方法，无需手动创建 CompletableFuture）
            processService.processFile(batchId, filePath, dataType);

            return ApiResponseDTO.success(fileMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadHospitalFile(MultipartFile file) {
        return uploadFile(file, "hospital");
    }

    @Override
    public ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadDrugStoreFile(MultipartFile file) {
        return uploadFile(file, "drugstore");
    }

    @Override
    public ApiResponseDTO<MdFuzzyMatchFileMessageDTO> uploadOrganizationFile(MultipartFile file) {
        return uploadFile(file, "organization");
    }

    /**
     * 保存文件到服务器
     */
    private String saveFile(MultipartFile file, String batchId) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = batchId + "_" + originalFilename;

        Path path = Paths.get(uploadDir, newFilename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }

    @Override
    public ApiResponseDTO<PageInfo<MdFuzzyMatchSummary>> getSummaryList(MdFuzzyMatchConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<MdFuzzyMatchSummary> summaryList = mdFuzzyMatchMapper.getSummaryList(condition);
            PageInfo<MdFuzzyMatchSummary> pageInfo = new PageInfo<>(summaryList);
            return ApiResponseDTO.success(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("获取数据失败：" + e.getMessage());
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportBatch(String batchId) {
        try {
            MdFuzzyMatchConditionDTO condition = new MdFuzzyMatchConditionDTO();
            condition.setBatchId(batchId);
            List<MdFuzzyMatchSummary> summaryList = mdFuzzyMatchMapper.getSummaryList(condition);

            if (summaryList == null || summaryList.isEmpty()) {
                return ApiResponseDTO.error("没有数据可导出");
            }

            // 使用自定义导出工具类
            byte[] excelData = mdFuzzyMatchToExcel.exportFuzzyMatchExcelFromEntity(summaryList, "模糊匹配结果");
            return ApiResponseDTO.success(excelData);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportSummary(MdFuzzyMatchConditionDTO condition) {
        try {
            List<MdFuzzyMatchSummary> summaryList = mdFuzzyMatchMapper.getSummaryList(condition);

            if (summaryList == null || summaryList.isEmpty()) {
                return ApiResponseDTO.error("没有数据可导出");
            }

            // 使用自定义导出工具类
            byte[] excelData = mdFuzzyMatchToExcel.exportFuzzyMatchExcelFromEntity(summaryList, "模糊匹配汇总");
            return ApiResponseDTO.success(excelData);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }

}
