package com.example.demo.service.impl;

import com.example.demo.apidata.ApiHaosen;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenCleanConditionDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.entity.HaoSenDataIu;
import com.example.demo.service.HaoSenLxDataService;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.HaoSenToLxEntity;
import com.example.demo.utils.MyBatisUtils;
import com.example.demo.utils.HaoSenToExcel;
import com.example.demo.vo.HaoSenCleanDataVO;
import com.example.demo.mapper.HaoSenCleanDataMapper;
import com.example.demo.mapper.HaoSenSqlMapper;
import com.example.demo.service.HaoSenInputCleanDataService;
import com.example.demo.utils.ReaderExcel;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import com.example.demo.vo.HaoSenOrganizationVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class HaoSenInputCleanDataServiceImpl implements HaoSenInputCleanDataService {


    @Autowired
    private HaoSenSqlMapper sqlMapper;

    @Autowired
    private HaoSenCleanDataMapper haosencleanDataMapper;

    @Autowired
    private HaoSenLxDataService haoSenLxDataService;


    @Value("${file.upload-dir}"+"/clean_file")
    private String uploadDir;
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


            // 2. 解析Excel
            ReaderExcel reader = new ReaderExcel();
            List<HaoSenInputAppealDataVO> cleanData = reader.readExcel(filePath.toString(), HaoSenInputAppealDataVO.class);

            if (cleanData.isEmpty()) {

                return ApiResponseDTO.error("清洗数据导入失败");

            }

            // 3. 数据库操作（事务内）
            sqlMapper.deleteAllHaoSenData();
            // 3. 清空并导入数据（事务操作）
            List<HaoSenDataIu> haoSenDataIuList = new ArrayList<>();
            HaoSenToLxEntity haoSenToLxEntity = new HaoSenToLxEntity();


            for (HaoSenInputAppealDataVO appeal : cleanData) {

                HaoSenOrganizationVO haoSenOrganization = new HaoSenOrganizationVO();
                BeanUtils.copyProperties(appeal, haoSenOrganization);
                sqlMapper.inputHaoSenUpdateData(haoSenOrganization);

                haoSenDataIuList.add(haoSenToLxEntity.ToLxColumn(haoSenOrganization));

            }

            haoSenLxDataService.lxUpdateData(haoSenDataIuList);

            // 4. 关键API调用
            String CleanMessage = new ApiHaosen().callExternalCleanDataApi();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(CleanMessage);
            if (rootNode.get("code").asInt() == 200){
                // 5. 全部成功后的响应
                fileMessage.setResult(cleanData.size()); // Changed from setProcessedCount to setProcessedCount
                fileMessage.setMessage("清洗数据推送成功");
                 return ApiResponseDTO.success(fileMessage);
            } else {

                return ApiResponseDTO.error("清洗数据推送失败");

            }


        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            return ApiResponseDTO.error("清洗数据推送失败");

        } catch (Exception e) {
            // 系统异常
            return ApiResponseDTO.error("系统处理异常");
        }

    }


    @Override
    public ApiResponseDTO<PageInfo<HaoSenCleanDataVO>> selectHaoSenCleanData(HaoSenCleanConditionDTO condition, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HaoSenCleanDataVO> haoSenCleanData = haosencleanDataMapper.selectHaoSenCleanData(condition);
            return ApiResponseDTO.success(new PageInfo<>(haoSenCleanData));

        } finally {
            PageHelper.clearPage();   // 必须清理 ThreadLocal

        }


    }



    @Override
    public ApiResponseDTO<HaoSenFileMessageDTO> cleanHaoSenData(HaoSenOrganizationVO haoSenOrganization) {

        HaoSenFileMessageDTO updateDataMessage = new HaoSenFileMessageDTO();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 1. 数据库操作
            sqlMapper.deleteAllHaoSenData();

            //插入数据
            sqlMapper.inputHaoSenUpdateData(haoSenOrganization);

            //  更新业务库逻辑
            List<HaoSenDataIu> haoSenDataIuList = new ArrayList<>();
            HaoSenToLxEntity haoSenToLxEntity = new HaoSenToLxEntity();
            haoSenDataIuList.add(haoSenToLxEntity.ToLxColumn(haoSenOrganization));
            haoSenLxDataService.lxUpdateData(haoSenDataIuList);

            //2. 关键API调用
            String updateMessage = new ApiHaosen().callExternalCleanDataApi();
            JsonNode rootNode = objectMapper.readTree(updateMessage);
            if (rootNode.get("code").asInt() == 200){

                updateDataMessage.setResult(1);
                updateDataMessage.setMessage("清洗数据推送成功");

                return ApiResponseDTO.success(updateDataMessage);
            } else {

                return ApiResponseDTO.error(rootNode.get("msg").asText());
            }


        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）

            return ApiResponseDTO.error("清洗数据推送失败");

        } catch (Exception e) {
            // 系统异常
            return ApiResponseDTO.error("系统处理异常");
        }


    }

    @Override
    public ApiResponseDTO<byte[]> exportCleanDataList(HaoSenCleanConditionDTO condition) {
        boolean needLimit = MyBatisUtils.isAllBlank(condition); // 自己写个工具判空

        List<HaoSenCleanDataVO> haoSenCleanData = haosencleanDataMapper.findAllHaoSenCleanData(condition,needLimit);

        if (haoSenCleanData == null || haoSenCleanData.isEmpty()) {
            return ApiResponseDTO.error("没有数据可导出");
        }

        HaoSenToExcel webToExcel = new HaoSenToExcel();
        byte[] excelBytes = webToExcel.exportCleanDataExcel(haoSenCleanData,"清洗数据");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("清洗数据数据.xlsx", StandardCharsets.UTF_8));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        return ApiResponseDTO.success(responseEntity.getBody());
    }


}
