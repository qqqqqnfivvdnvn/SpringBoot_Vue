package com.example.demo.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenDuplicateConditionDtO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.mapper.HaoSenDuplicateDataMapper;
import com.example.demo.service.HaoSenDuplicateDataService;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.HaoSenToDuplicateExcel;
import com.example.demo.vo.HaoSenCheckDuplicateDataVO;
import com.example.demo.vo.HaoSenDuplicateDataVO;
import com.example.demo.vo.HaoSenInputAppealDataVO;
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
import java.util.List;
import java.util.Objects;

@Service
@DS("slave_pg")
public class HaoSenDuplicateDataServiceImpl implements HaoSenDuplicateDataService {

    @Autowired
    private HaoSenDuplicateDataMapper haoSenDuplicateDataMapper;

    @Override
    public ApiResponseDTO<PageInfo<HaoSenCheckDuplicateDataVO>> getDuplicateData(HaoSenDuplicateConditionDtO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HaoSenCheckDuplicateDataVO> duplicateData = haoSenDuplicateDataMapper.getDuplicateData(condition);
            return ApiResponseDTO.success(new PageInfo<>(duplicateData));

        } finally  {
            PageHelper.clearPage();   // 必须清理 ThreadLocal
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponseDTO<Integer> updateDuplicateData() {
        return ApiResponseDTO.success(haoSenDuplicateDataMapper.updateDuplicateData());
    }


    @Override
    public ApiResponseDTO<PageInfo<HaoSenCheckDuplicateDataVO>> getDuplicateDataByCondition(HaoSenDuplicateConditionDtO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HaoSenCheckDuplicateDataVO> duplicateData = haoSenDuplicateDataMapper.getExportHSData(condition);
            return ApiResponseDTO.success(new PageInfo<>(duplicateData));
        } finally {
            PageHelper.clearPage();   // 必须清理 ThreadLocal
        }


    }

    @Override
    public ApiResponseDTO<byte[]> exportDuplicateData(HaoSenDuplicateConditionDtO condition) {
        List<HaoSenCheckDuplicateDataVO> duplicateData = haoSenDuplicateDataMapper.getExportHSData(condition);

        if (duplicateData == null || duplicateData.isEmpty()) {
            return ApiResponseDTO.error("没有数据可导出");
        }

        byte[] bytes = new HaoSenToDuplicateExcel().exportDuplicateDataExcel(duplicateData, "需要豪森确认编码");

        return ApiResponseDTO.success(bytes);

    }


//处理重复数据文件
    @Value("${file.upload-dir}"+"/duplicate_file")
    private String uploadDir;
    @Override
    public ApiResponseDTO<HaoSenFileMessageDTO> uploadDuplicateData(MultipartFile file) {
        try {
            HaoSenFileMessageDTO fileMessage = new HaoSenFileMessageDTO();
            // 1. 保存文件到服务器（非事务操作）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 2. 验证表头 - 传入预期的表头作为参数
            ExcelHeaderValidator.HeaderValidationResult headerResult =
                    ExcelHeaderValidator.validate(
                            filePath.toString(),
                            HaoSenDuplicateDataVO.EXPECTED_HEADERS
                    );

            // 3. 检查表头验证结果
            if (!headerResult.isValid()) {
                // 表头验证失败，直接返回错误信息
                return ApiResponseDTO.error("表头验证失败：" + headerResult.getMessage());
            }


            return ApiResponseDTO.success(fileMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("文件上传失败");

        }
    }


}
