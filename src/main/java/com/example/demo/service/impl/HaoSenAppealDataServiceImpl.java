package com.example.demo.service.impl;

import com.example.demo.apidata.ApiHaosen;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenAppealConditionDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.entity.HaoSenDataIu;
import com.example.demo.mapper.HaoSenAppealDataMapper;
import com.example.demo.mapper.HaoSenSqlMapper;
import com.example.demo.service.HaoSenAppealDataService;
import com.example.demo.service.HaoSenLxDataService;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.ReaderExcel;
import com.example.demo.utils.HaoSenToLxEntity;
import com.example.demo.utils.HaoSenToExcel;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import com.example.demo.vo.HaoSenOrganizationVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
public class HaoSenAppealDataServiceImpl implements HaoSenAppealDataService {
    @Autowired
    private HaoSenAppealDataMapper appealDataMapper;

    @Autowired
    private HaoSenSqlMapper sqlMapper;


    @Autowired
    private HaoSenLxDataService haoSenLxDataService;


    //申诉数据查看逻辑
    @Override
    public ApiResponseDTO<PageInfo<HaoSenOrganizationVO>> getAppealData(HaoSenAppealConditionDTO condition, int pageNum,
                                                                      int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<HaoSenOrganizationVO> appealData = appealDataMapper.getAppealData(condition);
            PageInfo<HaoSenOrganizationVO> pageInfo = new PageInfo<>(appealData);
            return ApiResponseDTO.success(pageInfo);

        } finally {

            PageHelper.clearPage();

        }


    }


    //申诉数据导出逻辑
    @Override
    public ApiResponseDTO<byte[]> exportAppealDataExcel(HaoSenAppealConditionDTO condition) {

        List<HaoSenOrganizationVO> appealData = appealDataMapper.getAppealData(condition);


        // 调用字段映射
        HaoSenToExcel webToExcel = new HaoSenToExcel();

        // 导出Excel
        byte[] excelBytes = webToExcel.exportConditionToExcel(appealData, "申诉数据");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);


        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("申诉数据.xlsx", StandardCharsets.UTF_8));


        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        return ApiResponseDTO.success(responseEntity.getBody());


    }


    //申诉数据导入逻辑
    @Value("${file.upload-dir}" + "/appeal_file")
    private String uploadDir;

    @Override
    public ApiResponseDTO<HaoSenFileMessageDTO> uploadAppealFile(MultipartFile file) {

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
            List<HaoSenInputAppealDataVO> appeals = reader.readExcel(filePath.toString(), HaoSenInputAppealDataVO.class);

            if (appeals.isEmpty()) {
                return ApiResponseDTO.error("文件内容为空");
            }


            List<HaoSenDataIu> haoSenDataIuList = new ArrayList<>();
            HaoSenToLxEntity haoSenToLxEntity = new HaoSenToLxEntity();

            //1、清空临时表
            sqlMapper.deleteAllHaoSenData();
            //2、写数据
            for (HaoSenInputAppealDataVO appeal : appeals) {
                HaoSenOrganizationVO haoSenOrganization = new HaoSenOrganizationVO();
                BeanUtils.copyProperties(appeal, haoSenOrganization);
                sqlMapper.inputHaoSenUpdateData(haoSenOrganization);
                haoSenDataIuList.add(haoSenToLxEntity.ToLxColumn(haoSenOrganization));
            }

            //3 、更新业务库数据
            haoSenLxDataService.lxUpdateData(haoSenDataIuList);


            // 4. 关键API调用
            String AppealMessage = new ApiHaosen().callExternalAppealApi();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(AppealMessage);

            if (rootNode.get("code").asInt() == 200) {
                // 5. 全部成功后的响应
                fileMessage.setResult(appeals.size());
                fileMessage.setMessage("申诉数据推送成功");
                return ApiResponseDTO.success(fileMessage);
            } else {

                return ApiResponseDTO.error(rootNode.get("msg").asText());

            }



        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            return ApiResponseDTO.error("申诉数据推送失败");

        } catch (Exception e) {
            // 系统异常
            return ApiResponseDTO.error("系统处理异常");
        }



    }


    //页面上单条处理申诉数据
    @Override
    public ApiResponseDTO<HaoSenFileMessageDTO> handleAppealData(HaoSenOrganizationVO haoSenOrganizationVO) {

        HaoSenFileMessageDTO appealDataMessage = new HaoSenFileMessageDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        //处理逻辑

        try {

            sqlMapper.deleteAllHaoSenData();
            sqlMapper.inputHaoSenUpdateData(haoSenOrganizationVO);

            String AppealMessage = new ApiHaosen().callExternalAppealApi();
            JsonNode rootNode = objectMapper.readTree(AppealMessage);

            if (rootNode.get("code").asInt() == 200) {

                 //  执行流向变更业务
                List<HaoSenDataIu> haoSenDataIuList = new ArrayList<>();
                HaoSenToLxEntity haoSenToLxEntity = new HaoSenToLxEntity();
                haoSenDataIuList.add(haoSenToLxEntity.ToLxColumn(haoSenOrganizationVO));
                haoSenLxDataService.lxUpdateData(haoSenDataIuList);

                appealDataMessage.setResult(1);
                appealDataMessage.setMessage("申诉数据推送成功");
                return ApiResponseDTO.success(appealDataMessage);

            } else {

                return ApiResponseDTO.error(rootNode.get("msg").asText());
            }


        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            return ApiResponseDTO.error("申诉数据推送失败");

        } catch (Exception e) {
            // 系统异常

            return ApiResponseDTO.error("系统处理异常");
        }


    }


}


