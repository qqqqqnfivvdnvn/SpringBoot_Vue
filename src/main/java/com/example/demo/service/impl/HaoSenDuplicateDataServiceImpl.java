package com.example.demo.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenDuplicateConditionDtO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.dto.HaoSenUpdateStatusDTO;
import com.example.demo.mapper.HaoSenDuplicateDataMapper;
import com.example.demo.mapper.HaoSenUpdateDataMapper;
import com.example.demo.service.HaoSenDuplicateDataService;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.HaoSenToDuplicateExcel;
import com.example.demo.utils.ReaderExcel;
import com.example.demo.vo.HaoSenCheckDuplicateDataVO;
import com.example.demo.vo.HaoSenDuplicateDataVO;
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

    @Autowired
    private HaoSenUpdateDataMapper haoSenUpdateDataMapper;

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
            int updatedCount = 0;

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

            // 4. 使用 ReaderExcel 读取文件内容
            ReaderExcel readerExcel = new ReaderExcel();
            List<HaoSenDuplicateDataVO> dataList = readerExcel.readExcel(filePath.toString(), HaoSenDuplicateDataVO.class);

            // 5. 遍历数据，处理"是否需易联禁用这条对应关系"列为"是"的数据
            for (HaoSenDuplicateDataVO data : dataList) {
                if ("是".equals(data.getYlRemark())) {
                    HaoSenUpdateStatusDTO updateStatus = new HaoSenUpdateStatusDTO();
                    updateStatus.setDataId(data.getDataId());
                    updateStatus.setStatus(4); // 4 表示豪森禁用客户
                    updateStatus.setRemark("豪森客户标记为禁用");

                    // 根据机构类型调用对应的更新方法
                    String orgType = data.getOrgType();
                    if ("医院".equals(orgType)) {
                        haoSenUpdateDataMapper.updateHospitalStatus(updateStatus);
                    } else if ("药店".equals(orgType)) {
                        haoSenUpdateDataMapper.updateDrugStoreStatus(updateStatus);
                    } else if ("商业".equals(orgType)) {
                        haoSenUpdateDataMapper.updateCompanyStatus(updateStatus);
                    }

                    updatedCount++;
                }
            }

            fileMessage.setResult(updatedCount);
            fileMessage.setMessage("上传成功，共处理 " + dataList.size() + " 条数据，更新 " + updatedCount + " 条数据状态为禁用");

            return ApiResponseDTO.success(fileMessage);

        } catch (Exception e) {
//            e.printStackTrace();
            return ApiResponseDTO.error("文件上传失败：" + e.getMessage());
        }
    }


}
