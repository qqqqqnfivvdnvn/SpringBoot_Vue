package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.MdLocationBatchConditionDTO;
import com.example.demo.entity.MdLocationBatch;
import com.example.demo.mapper.MdLocationBatchMapper;
import com.example.demo.service.MdLocationBatchService;
import com.example.demo.vo.MdLocationBatchVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 主数据地理位置批次管理 Service 实现
 */
@Service
public class MdLocationBatchServiceImpl implements MdLocationBatchService {

    @Autowired
    private MdLocationBatchMapper batchMapper;

    @Autowired
    private MdLocationBatchAsyncService batchAsyncService;

    @Value("${file.upload-dir:./}")
    private String uploadDir;

    @Override
    public ApiResponseDTO<PageInfo<MdLocationBatchVO>> getBatchList(MdLocationBatchConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            var batchList = batchMapper.selectByCondition(condition);
            PageInfo<MdLocationBatchVO> pageInfo = new PageInfo<>(batchList);
            return ApiResponseDTO.success(pageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO<?> uploadBatchFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ApiResponseDTO.error("上传文件不能为空");
        }

        try {
            // 1. 保存文件到服务器
            Path uploadPath = Paths.get(uploadDir, "maindata_location_file");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResponseDTO.error("文件名不能为空");
            }

            // 生成唯一文件名：UUID_原始文件名
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 2. 创建批次记录（状态为 0 处理中）
            String batchId = UUID.randomUUID().toString();
            MdLocationBatch batch = new MdLocationBatch();
            batch.setBatchId(batchId);
            batch.setCreateTime(LocalDateTime.now());
            batch.setUpdateTime(LocalDateTime.now());
            batch.setStatus(0); // 0-处理中
            batch.setOriginalFilename(originalFilename);
            batchMapper.insert(batch);

            // 3. 异步处理数据（包含表头验证、解析、地理位置查询、入库）
            batchAsyncService.processBatchData(batchId, filePath.toString(), originalFilename);

            return ApiResponseDTO.success("文件上传成功，正在后台处理中...");

        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "未知错误";
            return ApiResponseDTO.error("文件上传失败：" + errorMsg);
        }
    }
}
