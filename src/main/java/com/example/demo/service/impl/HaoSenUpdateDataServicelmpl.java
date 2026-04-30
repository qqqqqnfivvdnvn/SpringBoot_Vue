package com.example.demo.service.impl;


import com.example.demo.apidata.ApiHaosen;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.dto.HaoSenUpdateStatusDTO;
import com.example.demo.entity.HaoSenDataIu;
import com.example.demo.mapper.HaoSenUpdateDataMapper;
import com.example.demo.service.HaoSenLxDataService;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.HaoSenToLxEntity;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import com.example.demo.vo.HaoSenOrganizationVO;
import com.example.demo.mapper.HaoSenSqlMapper;
import com.example.demo.service.HaoSenUpdateDataService;
import com.example.demo.utils.ReaderExcel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class HaoSenUpdateDataServicelmpl implements HaoSenUpdateDataService {


    @Autowired
    private  HaoSenSqlMapper sqlMapper;


    @Autowired
    private HaoSenLxDataService haoSenLxDataService;

    @Autowired
    private HaoSenUpdateDataMapper haoSenUpdateDataMapper;


    @Value("${file.upload-dir}"+"/update_file")
    private String uploadDir;

    //导入更新数据的文件接口
    @Override
    public ApiResponseDTO<HaoSenFileMessageDTO> uploadUpdateFile(MultipartFile file) {
        HaoSenFileMessageDTO fileMessage = new HaoSenFileMessageDTO();

        try {
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
                            HaoSenInputAppealDataVO.EXPECTED_HEADERS  // 将预期表头作为参数传入
                    );

            // 3. 检查表头验证结果
            if (!headerResult.isValid()) {
                // 表头验证失败，直接返回错误信息

                return ApiResponseDTO.error("表头验证失败：" + headerResult.getMessage());
            }

            // 4. 流式读取 Excel，分批处理（避免全量数据驻留内存）
            ReaderExcel reader = new ReaderExcel();
            List<HaoSenDataIu> haoSenDataIuList = new ArrayList<>();
            HaoSenToLxEntity haoSenToLxEntity = new HaoSenToLxEntity();
            AtomicInteger totalCount = new AtomicInteger(0);

            // 清空临时表
            sqlMapper.deleteAllHaoSenData();

            // 流式读取 + 分批处理
            reader.readExcelStreaming(filePath.toString(), HaoSenInputAppealDataVO.class, batch -> {
                // 转换为 HaoSenOrganizationVO 列表
                List<HaoSenOrganizationVO> orgList = new ArrayList<>();
                for (HaoSenInputAppealDataVO appeal : batch) {
                    HaoSenOrganizationVO haoSenOrganization = new HaoSenOrganizationVO();
                    BeanUtils.copyProperties(appeal, haoSenOrganization);
                    orgList.add(haoSenOrganization);
                    // 累积流向变更数据
                    haoSenDataIuList.add(haoSenToLxEntity.ToLxColumn(haoSenOrganization));
                }
                // 批量插入临时表
                sqlMapper.batchInputHaoSenUpdateData(orgList);
                totalCount.addAndGet(batch.size());

                // 流向变更数据分批提交（每 400 条）
                if (haoSenDataIuList.size() >= 400) {
                    haoSenLxDataService.lxUpdateData(new ArrayList<>(haoSenDataIuList));
                    haoSenDataIuList.clear();
                }
            }, ReaderExcel.BATCH_SIZE_47_FIELDS);

            // 检查是否有数据
            if (totalCount.get() == 0) {
                return ApiResponseDTO.error("文件内容为空");
            }

            // 处理剩余流向变更数据
            if (!haoSenDataIuList.isEmpty()) {
                haoSenLxDataService.lxUpdateData(haoSenDataIuList);
            }

            // 5. 关键API调用
            String updateMessage = new ApiHaosen().callExternalUpdateApi();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(updateMessage);
            if (rootNode.get("code").asInt() == 200){
                // 5. 全部成功后的响应
                fileMessage.setResult(totalCount.get());
                fileMessage.setMessage("更新数据推送成功");
                return ApiResponseDTO.success(fileMessage);
            } else {

                return ApiResponseDTO.error(rootNode.get("msg").asText());
            }




        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            return ApiResponseDTO.error("更新数据推送失败");
        } catch (Exception e) {
            // 系统异常
            return ApiResponseDTO.error("系统处理异常");
        }

    }



    //获取药店、医疗机构大库的单条数据
    @Override
    public ApiResponseDTO<HaoSenOrganizationVO> findDaKuData(String keyid) {
        return ApiResponseDTO.success(sqlMapper.findDaKuData(keyid));
    }



    //更新医院、药店、商业信息，并通过接口推送
    @Override
    public ApiResponseDTO<HaoSenFileMessageDTO> updateOneUpdateData(HaoSenOrganizationVO haoSenOrganization) {


        HaoSenFileMessageDTO updateDataMessage = new HaoSenFileMessageDTO();
        ObjectMapper objectMapper = new ObjectMapper();


        try {


            //  更新业务库逻辑
            List<HaoSenDataIu> haoSenDataIuList = new ArrayList<>();
            HaoSenToLxEntity haoSenToLxEntity = new HaoSenToLxEntity();
            haoSenDataIuList.add(haoSenToLxEntity.ToLxColumn(haoSenOrganization));
            haoSenLxDataService.lxUpdateData(haoSenDataIuList);


            // 1. 数据库操作
            sqlMapper.deleteAllHaoSenData();
            //插入数据
            sqlMapper.inputHaoSenUpdateData(haoSenOrganization);

            //2. 关键API调用
            String updateMessage = new ApiHaosen().callExternalUpdateApi();
            JsonNode rootNode = objectMapper.readTree(updateMessage);

            if (rootNode.get("code").asInt() == 200){

                updateDataMessage.setResult(1);
                updateDataMessage.setMessage("更新数据推送成功");
                return ApiResponseDTO.success(updateDataMessage);

            } else {

                return ApiResponseDTO.error(rootNode.get("msg").asText());
            }




        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            return ApiResponseDTO.error("更新数据推送失败");

        } catch (Exception e) {
            // 系统异常
            return ApiResponseDTO.error("系统处理异常");
        }



    }


    //更新医院、药店、商业的状态 status 相关字段 status  1 正常,2 作废,3 无法清洗,4 豪森禁用客户,5 重复数据
    @Override
    public ApiResponseDTO<Integer> updateInstitutionType(HaoSenUpdateStatusDTO haoSenUpdateStatusDTO) {
        String institutionType = haoSenUpdateStatusDTO.getInstitutionType();

        if (institutionType == null || institutionType.equals("")) {
            return ApiResponseDTO.error("机构类型不能为空");
        } else if (institutionType.equals("hospital")) {
            return ApiResponseDTO.success(haoSenUpdateDataMapper.updateHospitalStatus(haoSenUpdateStatusDTO));
        } else if (institutionType.equals("drugStore")) {
            return ApiResponseDTO.success(haoSenUpdateDataMapper.updateDrugStoreStatus(haoSenUpdateStatusDTO));
        } else if (institutionType.equals("company")) {
            return ApiResponseDTO.success(haoSenUpdateDataMapper.updateCompanyStatus(haoSenUpdateStatusDTO));
        }
        return ApiResponseDTO.error("机构类型不存在");

    }


// 删除机构数据
    @Override
    public ApiResponseDTO<Integer> deleteInstitutionData(Map<String, String> params) {
        String institutionType = params.get("institutionType");
        String dataId = params.get("dataId");
        if (institutionType == null || dataId == null || institutionType.equals("") || dataId.equals("")) {
            return ApiResponseDTO.error("机构类型或数据ID不能为空");
        } else if (institutionType.equals("hospital")) {
            return ApiResponseDTO.success(haoSenUpdateDataMapper.deleteHospitalDataById(dataId));
        } else if (institutionType.equals("drugStore")) {
            return ApiResponseDTO.success(haoSenUpdateDataMapper.deleteDrugStoreDataById(dataId));
        } else if (institutionType.equals("company")) {
            return ApiResponseDTO.success(haoSenUpdateDataMapper.deleteCompanyDataById(dataId));
        }
        return ApiResponseDTO.error("机构类型不存在");

    }


}
