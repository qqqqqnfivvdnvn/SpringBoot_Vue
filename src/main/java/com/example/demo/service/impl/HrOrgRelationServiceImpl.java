package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HrOrgRelationConditionDTO;
import com.example.demo.entity.HrMonitoringData;
import com.example.demo.entity.HrOrgRelation;
import com.example.demo.mapper.HrMonitoringDataMapper;
import com.example.demo.mapper.HrOrgRelationMapper;
import com.example.demo.service.HrOrgRelationService;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.HrToExcel;
import com.example.demo.utils.ReaderExcel;
import com.example.demo.vo.HrOrgRelationImportVO;
import com.example.demo.vo.HrOrgRelationVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 恒瑞数据比对关系 Service 实现
 */
@Service
public class HrOrgRelationServiceImpl implements HrOrgRelationService {

    @Autowired
    private HrOrgRelationMapper orgRelationMapper;

    @Autowired
    private HrMonitoringDataMapper monitoringDataMapper;

    @Value("${file.upload-dir:/./}")
    private String uploadDir;

    @Override
    public ApiResponseDTO<PageInfo<HrOrgRelationVO>> getRelationList(HrOrgRelationConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HrOrgRelation> dataList = orgRelationMapper.selectByCondition(condition);

            // 使用 PageHelper 返回的 Page 对象创建 PageInfo，保留分页信息
            PageInfo<HrOrgRelation> pageInfo = new PageInfo<>(dataList);

            // 转换 VO 列表
            List<HrOrgRelationVO> voList = new ArrayList<>();
            for (HrOrgRelation entity : dataList) {
                HrOrgRelationVO vo = new HrOrgRelationVO();
                BeanUtils.copyProperties(entity, vo);
                voList.add(vo);
            }

            // 创建 VO 的 PageInfo，保留原始分页信息
            PageInfo<HrOrgRelationVO> voPageInfo = new PageInfo<>();
            BeanUtils.copyProperties(pageInfo, voPageInfo);
            voPageInfo.setList(voList);

            return ApiResponseDTO.success(voPageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<String> deleteRelation(String businessLicenseName) {
        try {
            int result = orgRelationMapper.deleteById(businessLicenseName);
            if (result > 0) {
                return ApiResponseDTO.success("删除成功");
            } else {
                return ApiResponseDTO.error("删除失败，记录不存在");
            }
        } catch (Exception e) {
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<String> updateRelation(HrOrgRelationVO relationVO) {
        try {
            HrOrgRelation relation = new HrOrgRelation();
            BeanUtils.copyProperties(relationVO, relation);
            int result = orgRelationMapper.update(relation);
            if (result > 0) {
                return ApiResponseDTO.success("更新成功");
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<String> addRelation(HrOrgRelationVO relationVO) {
        try {
            // 校验必填字段
            if (relationVO.getBusinessLicenseName() == null || relationVO.getBusinessLicenseName().trim().isEmpty()) {
                return ApiResponseDTO.error("营业执照名称不能为空");
            }

            HrOrgRelation relation = new HrOrgRelation();
            BeanUtils.copyProperties(relationVO, relation);

            int result = orgRelationMapper.insert(relation);
            if (result > 0) {
                return ApiResponseDTO.success("添加成功");
            } else {
                return ApiResponseDTO.error("添加失败");
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("duplicate") || errorMsg.contains("重复") || errorMsg.contains("PRIMARY KEY"))) {
                return ApiResponseDTO.error("添加失败：该营业执照名称已存在");
            }
            return ApiResponseDTO.error("添加失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<byte[]> exportRelationExcel(HrOrgRelationConditionDTO condition) {
        try {
            List<HrOrgRelation> dataList = orgRelationMapper.selectByCondition(condition);

            if (dataList == null || dataList.isEmpty()) {
                return ApiResponseDTO.error("没有数据可导出");
            }

            List<HrOrgRelationVO> voList = new ArrayList<>();
            for (HrOrgRelation entity : dataList) {
                HrOrgRelationVO vo = new HrOrgRelationVO();
                BeanUtils.copyProperties(entity, vo);
                voList.add(vo);
            }

            HrToExcel hrToExcel = new HrToExcel();
            byte[] excelBytes = hrToExcel.exportOrgRelationExcel(voList, "恒瑞比对关系");

            return ApiResponseDTO.success(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导出失败：" + e.getMessage());
        }
    }


    @Override
    public ApiResponseDTO<String> importRelationExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ApiResponseDTO.error("上传文件不能为空");
        }

        try {
            // 保存文件到服务器
            Path uploadPath = Paths.get(uploadDir, "hengrui_file");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResponseDTO.error("文件名不能为空");
            }

            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 1. 验证表头
            ExcelHeaderValidator.HeaderValidationResult headerResult =
                    ExcelHeaderValidator.validate(
                            filePath.toString(),
                            HrOrgRelationImportVO.EXPECTED_HEADERS
                    );

            if (headerResult == null || !headerResult.isValid()) {
                String errorMsg = headerResult != null && headerResult.getMessage() != null
                        ? headerResult.getMessage() : "表头验证失败，请检查 Excel 表头格式";
                throw new RuntimeException(errorMsg);
            }

            // 2. 解析 Excel
            ReaderExcel reader = new ReaderExcel();
            List<HrOrgRelationImportVO> dataList = reader.readExcel(filePath.toString(), HrOrgRelationImportVO.class);

            if (dataList == null || dataList.isEmpty()) {
                return ApiResponseDTO.error("文件内容为空");
            }

            // 3. 转换数据为 HrMonitoringData 实体（用于插入到 hr_monitoring_data_tmp 表）
            List<HrMonitoringData> insertList = new ArrayList<>();
            String batchId = UUID.randomUUID().toString();

            for (HrOrgRelationImportVO vo : dataList) {
                // 校验必填字段：营业执照名称和 keyid 不能为空
                if (vo.getBusinessLicenseName() == null || vo.getBusinessLicenseName().trim().isEmpty()) {
                    continue;
                }
                if (vo.getKeyId() == null || vo.getKeyId().trim().isEmpty()) {
                    continue;
                }

                HrMonitoringData data = new HrMonitoringData();
                data.setBatchId(batchId);
                data.setBusinessLicenseName(vo.getBusinessLicenseName().trim());
                data.setStandardizedProvince(vo.getProvince() != null ? vo.getProvince().trim() : "");
                data.setKeyId(vo.getKeyId().trim());
                data.setName(vo.getName() != null ? vo.getName().trim() : "");
                data.setAddress(vo.getAddress() != null ? vo.getAddress().trim() : "");
                data.setCreateTime(LocalDateTime.now());
                data.setUpdateTime(LocalDateTime.now());

                insertList.add(data);
            }

            if (insertList.isEmpty()) {
                return ApiResponseDTO.error("没有有效数据可导入（营业执照名称和 keyid 不能为空）");
            }

            // 4. 分批批量插入到 hr_monitoring_data_tmp 表（每批 50 条，避免 SQL Server 2100 参数限制）
            int batchSize = 50;
            for (int i = 0; i < insertList.size(); i += batchSize) {
                int end = Math.min(i + batchSize, insertList.size());
                List<HrMonitoringData> subList = insertList.subList(i, end);
                monitoringDataMapper.batchInsert(subList);
            }

            // 5. 同步到 hr_org_relation 表
            int insertedCount = orgRelationMapper.syncFromTemp(batchId);

            // 6. 清理临时表数据
            monitoringDataMapper.deleteByBatchId(batchId);

            return ApiResponseDTO.success("导入完成：成功插入 " + insertedCount + " 条记录");

        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 200) {
                errorMsg = errorMsg.substring(0, 200) + "...";
            }
            return ApiResponseDTO.error("文件上传失败：" + errorMsg);
        }
    }
}
