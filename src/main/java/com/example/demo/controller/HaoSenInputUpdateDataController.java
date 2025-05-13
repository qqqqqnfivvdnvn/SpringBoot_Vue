package com.example.demo.controller;


import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.service.impl.HaoSenInputAppealService;
import com.example.demo.utils.HaoSenAppealExcelReader;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Controller

@RequestMapping("/updateData")
public class HaoSenInputUpdateDataController {

    //    导入更新数据

    @Autowired
    private HaoSenInputAppealService inputAppealService;

    @Value("${file.upload-dir}"+"\\update_file")
    private String uploadDir;


    @PostMapping("/importUpdateData")
    public ResponseEntity<ApiResponseDTO<HaoSenFileMessageDTO>> importAppealData(@RequestParam("file") MultipartFile file) {
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
            List<HaoSenInputAppealDataVO> updateData = reader.readExcel(filePath.toString(), HaoSenInputAppealDataVO.class);

            if (updateData.isEmpty()) {
                fileMessage.setMessage("文件内容为空");
                fileMessage.setAppealMessage("更新数据导入失败");
                return ResponseEntity.ok(ApiResponseDTO.success(fileMessage));
            }

            // 3. 数据库操作（事务内）
            inputAppealService.deleteAllAppeal();
            // 3. 清空并导入数据（事务操作）
            for (HaoSenInputAppealDataVO appeal : updateData) {
                inputAppealService.inputAppeal(appeal);
            }

            // 4. 关键API调用（必须在事务内）
            String AppealMessage = callExternalAppealApi();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(AppealMessage);
            if (rootNode.get("code").asInt() == 200){
                // 5. 全部成功后的响应
                fileMessage.setProcessedCount(updateData.size());
                fileMessage.setMessage("success");
                fileMessage.setAppealMessage("更新数据推送成功");
            } else {
                fileMessage.setMessage(rootNode.get("msg").asText());
                fileMessage.setAppealMessage("更新数据推送失败");
            }
            return ResponseEntity.ok(ApiResponseDTO.success(fileMessage));

        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            fileMessage.setMessage("fail");
            fileMessage.setAppealMessage("更新数据推送失败");
            return ResponseEntity.ok(ApiResponseDTO.error("更新数据推送失败"));
        } catch (Exception e) {
            // 系统异常
            fileMessage.setMessage("系统处理异常");
            fileMessage.setAppealMessage("更新数据处理失败");
            return ResponseEntity.ok(ApiResponseDTO.success(fileMessage));
        }

    }

    
    
    private String callExternalAppealApi() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://192.168.33.9:8000/update_data");

            // 设置合理超时（10秒）
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(1000)
                    .setSocketTimeout(1000)
                    .build();
            request.setConfig(config);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // 获取完整响应内容
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            }
        } catch (Exception e) {

            return "接口调用失败";
        }
    }
}

    

