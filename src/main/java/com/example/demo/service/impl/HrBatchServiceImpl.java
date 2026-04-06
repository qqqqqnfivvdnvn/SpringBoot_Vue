package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.dto.HrBatchConditionDTO;
import com.example.demo.entity.HrBatch;
import com.example.demo.mapper.HrBatchMapper;
import com.example.demo.mapper.HrMonitoringDataMapper;
import com.example.demo.service.HrBatchService;
import com.example.demo.vo.HrBatchExportVO;
import com.example.demo.vo.HrBatchVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.alibaba.excel.EasyExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 恒瑞批次管理 Service 实现
 */
@Service
public class HrBatchServiceImpl implements HrBatchService {

    @Autowired
    private HrBatchMapper batchMapper;

    @Autowired
    private HrBatchAsyncService batchAsyncService;

    @Autowired
    private HrMonitoringDataMapper monitoringDataMapper;

    @Value("${file.upload-dir:/./}")
    private String uploadDir;

    @Override
    public ApiResponseDTO<PageInfo<HrBatchVO>> getBatchList(HrBatchConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HrBatchVO> batchList = batchMapper.selectByCondition(condition);
            PageInfo<HrBatchVO> pageInfo = new PageInfo<>(batchList);
            return ApiResponseDTO.success(pageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO<HaoSenFileMessageDTO> uploadBatchFile(MultipartFile file) {
        HaoSenFileMessageDTO fileMessage = new HaoSenFileMessageDTO();

        if (file == null || file.isEmpty()) {
            return ApiResponseDTO.error("上传文件不能为空");
        }

        try {
            // 1. 保存文件到服务器
            Path uploadPath = Paths.get(uploadDir, "hengrui_file");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // 获取原始文件名并处理中文
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResponseDTO.error("文件名不能为空");
            }
            // 生成唯一文件名：UUID_原始文件名（保留中文）
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 2. 创建批次记录（状态为 0 处理中）
            String batchId = UUID.randomUUID().toString();
            HrBatch batch = new HrBatch();
            batch.setBatchId(batchId);
            batch.setCreateTime(LocalDateTime.now());
            batch.setUpdateTime(LocalDateTime.now());
            batch.setStatus(0); // 0-处理中
            batch.setOriginalFilename(originalFilename);
            batchMapper.insert(batch);

            // 3. 异步处理数据（包含表头验证、解析、入库）
            batchAsyncService.processBatchData(batchId, filePath.toString(), originalFilename);

            fileMessage.setMessage("文件上传成功，正在后台处理中...");
            return ApiResponseDTO.success(fileMessage);

        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "未知错误";
            return ApiResponseDTO.error("文件上传失败：" + errorMsg);
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportBatchExcel(HrBatchConditionDTO condition) {
        try {
            // 根据批次 ID 查询导出数据
            List<HrBatchExportVO> dataList = monitoringDataMapper.selectExportDataByBatchId(condition.getBatchId());

            if (dataList == null || dataList.isEmpty()) {
                return ApiResponseDTO.error("该批次没有数据");
            }

            // 使用 EasyExcel 导出，使用导出 VO 类定义表头
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EasyExcel.write(outputStream, HrBatchExportVO.class)
                    .sheet("批次数据")
                    .doWrite(dataList);

            return ApiResponseDTO.success(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }
}
