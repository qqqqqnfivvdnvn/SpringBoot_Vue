package com.example.demo.service;

import com.example.demo.apidata.ApiHaosen;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.mapper.HaoSenSqlMapper;
import com.example.demo.service.impl.HaoSenInputCleanDataService;
import com.example.demo.utils.HaoSenAppealExcelReader;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class HaoSenInputCleanDataServiceImpl implements HaoSenInputCleanDataService {


    @Autowired
    private HaoSenSqlMapper sqlMapper;


    @Value("${file.upload-dir}"+"\\clean_file")
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

            // 2. 解析Excel
            HaoSenAppealExcelReader reader = new HaoSenAppealExcelReader();
            List<HaoSenInputAppealDataVO> cleanData = reader.readExcel(filePath.toString(), HaoSenInputAppealDataVO.class);

            if (cleanData.isEmpty()) {
                fileMessage.setMessage("文件内容为空");
                fileMessage.setAppealMessage("清洗数据导入失败");
                return ApiResponseDTO.success(fileMessage);
            }

            // 3. 数据库操作（事务内）
            sqlMapper.deleteAllHaoSenData();
            // 3. 清空并导入数据（事务操作）

            for (HaoSenInputAppealDataVO appeal : cleanData) {

                sqlMapper.inputHaoSenData(appeal);

            }

            // 4. 关键API调用（必须在事务内）
            String CleanMessage = new ApiHaosen().callExternalCleanDataApi();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(CleanMessage);
            if (rootNode.get("code").asInt() == 200){
                // 5. 全部成功后的响应
                fileMessage.setProcessedCount(cleanData.size());
                fileMessage.setMessage("success");
                fileMessage.setAppealMessage("清洗数据推送成功");
            } else {
                fileMessage.setMessage(rootNode.get("msg").asText());
                fileMessage.setAppealMessage("清洗数据推送失败");
            }
            return ApiResponseDTO.success(fileMessage);

        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            fileMessage.setMessage("fail");
            fileMessage.setAppealMessage("清洗数据推送失败");
            return ApiResponseDTO.success(fileMessage);
        } catch (Exception e) {
            // 系统异常
            fileMessage.setMessage("系统处理异常");
            fileMessage.setAppealMessage("清洗数据处理失败");
            return ApiResponseDTO.success(fileMessage);
        }

        ////       测试数据
//
//        fileMessage.setProcessedCount(10);
//        fileMessage.setMessage("success");
//        fileMessage.setAppealMessage("清洗数据推送成功");
//        return ApiResponseDTO.success(fileMessage);

    }




}
