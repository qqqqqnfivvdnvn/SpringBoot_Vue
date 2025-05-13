package com.example.demo.controller;

import com.example.demo.dto.FileMessageDTO;
import com.example.demo.service.impl.InputAppealService;
import com.example.demo.utils.AppealExcelReader;
import com.example.demo.vo.AppealDataVO;
import com.example.demo.vo.InputAppealDataVO;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import org.apache.http.impl.client.CloseableHttpClient;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.AppealConditionDTO;
import com.example.demo.service.impl.AppealDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller

@RequestMapping("/appealData")

public class AppealDataController {

    @Autowired
    private AppealDataService appealDataService;


    @RequestMapping("/getAppealData")
    public ResponseEntity<ApiResponseDTO<Page<AppealDataVO>>> getAppealData(AppealConditionDTO condition, Pageable pageable) {
        ApiResponseDTO<Page<AppealDataVO>> appealData = appealDataService.getAppealData(condition, pageable);
        return ResponseEntity.ok(appealData);
    }

//    导出申诉数据

    @RequestMapping("/exportAppealData")
    ResponseEntity<byte[]> exportAppealData(AppealConditionDTO condition, Pageable pageable) throws UnsupportedEncodingException {

        ApiResponseDTO<Page<AppealDataVO>> appealData = appealDataService.getAppealData(condition, pageable);
        List<AppealDataVO> dataList = appealData.getData().getContent();

        // 导出Excel
        byte[] excelBytes = exportToExcel(dataList);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                URLEncoder.encode("申诉数据.xlsx", StandardCharsets.UTF_8.toString()));

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }


    private byte[] exportToExcel(List<AppealDataVO> dataList) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            // 创建工作表
            XSSFSheet sheet = workbook.createSheet("申诉数据");

            // 创建表头
            String[] headers = {
                    "批次编号", "data_id", "数据类型", "原始数据编码", "原始数据名称",
                    "省份", "original_address", "经销商", "申诉备注", "申诉解决",
                    "机构类型", "keyid", "医院名称", "历史名称", "省",
                    "省ID", "市", "市ID", "区县", "区县ID",
                    "地址", "等级", "等次", "所有制", "类别",
                    "总分院kid", "总分院名称", "军队医院", "登记号", "有效期",
                    "诊疗科室", "法人代表", "统一社会信用代码", "经营方式", "经营范围",
                    "总分店kid", "总分店名称", "成立时间", "注册资金", "企业类型",
                    "登记状态", "所属行业", "登记机关"
            };
            // 创建表头行
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // 填充数据
            int rowNum = 1;
            for (AppealDataVO data : dataList) {
                XSSFRow row = sheet.createRow(rowNum++);
                int colNum = 0;
                row.createCell(colNum++).setCellValue(data.getBatchCode() != null ? data.getBatchCode() : "");
                row.createCell(colNum++).setCellValue(data.getDataId() != null ? data.getDataId() : "");
                row.createCell(colNum++).setCellValue(data.getDataType() != null ? data.getDataType() : "");
                row.createCell(colNum++).setCellValue(data.getDataCode() != null ? data.getDataCode() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalName() != null ? data.getOriginalName() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalProvince() != null ? data.getOriginalProvince() : "");
                row.createCell(colNum++).setCellValue(data.getOriginalAddress() != null ? data.getOriginalAddress() : "");
                row.createCell(colNum++).setCellValue(data.getCompanyName() != null ? data.getCompanyName() : "");
                row.createCell(colNum++).setCellValue(data.getAppealRemark() != null ? data.getAppealRemark() : "");
                row.createCell(colNum++).setCellValue(data.getSolveRemark() != null ? data.getSolveRemark() : "");
                row.createCell(colNum++).setCellValue(data.getInstitutionType() != null ? data.getInstitutionType() : "");
                row.createCell(colNum++).setCellValue(data.getKeyid() != null ? data.getKeyid() : "");
                row.createCell(colNum++).setCellValue(data.getName() != null ? data.getName() : "");
                row.createCell(colNum++).setCellValue(data.getNameHistory() != null ? data.getNameHistory() : "");
                row.createCell(colNum++).setCellValue(data.getProvince() != null ? data.getProvince() : "");
                row.createCell(colNum++).setCellValue(data.getProvinceid() != null ? data.getProvinceid() : "");
                row.createCell(colNum++).setCellValue(data.getCity() != null ? data.getCity() : "");
                row.createCell(colNum++).setCellValue(data.getCityid() != null ? data.getCityid() : "");
                row.createCell(colNum++).setCellValue(data.getArea() != null ? data.getArea() : "");
                row.createCell(colNum++).setCellValue(data.getAreaid() != null ? data.getAreaid() : "");
                row.createCell(colNum++).setCellValue(data.getKuAddress() != null ? data.getKuAddress() : "");
                row.createCell(colNum++).setCellValue(data.getLevel() != null ? data.getLevel() : "");
                row.createCell(colNum++).setCellValue(data.getGrade() != null ? data.getGrade() : "");
                row.createCell(colNum++).setCellValue(data.getPublicflag() != null ? data.getPublicflag() : "");
                row.createCell(colNum++).setCellValue(data.getClassify() != null ? data.getClassify() : "");
                row.createCell(colNum++).setCellValue(data.getGeneralBranchKid() != null ? data.getGeneralBranchKid() : "");
                row.createCell(colNum++).setCellValue(data.getGeneralBranchName() != null ? data.getGeneralBranchName() : "");
                row.createCell(colNum++).setCellValue(data.getMilitaryHos() != null ? data.getMilitaryHos() : "");
                row.createCell(colNum++).setCellValue(data.getRegcode() != null ? data.getRegcode() : "");
                row.createCell(colNum++).setCellValue(data.getValidity() != null ? data.getValidity() : "");
                row.createCell(colNum++).setCellValue(data.getSubjects() != null ? data.getSubjects() : "");
                row.createCell(colNum++).setCellValue(data.getLegalperson() != null ? data.getLegalperson() : "");
                row.createCell(colNum++).setCellValue(data.getUsci() != null ? data.getUsci() : "");
                row.createCell(colNum++).setCellValue(data.getOperation() != null ? data.getOperation() : "");
                row.createCell(colNum++).setCellValue(data.getScope() != null ? data.getScope() : "");
                row.createCell(colNum++).setCellValue(data.getMainBranchKid() != null ? data.getMainBranchKid() : "");
                row.createCell(colNum++).setCellValue(data.getMainBranchName() != null ? data.getMainBranchName() : "");
                row.createCell(colNum++).setCellValue(data.getCreateDate() != null ? data.getCreateDate() : "");
                row.createCell(colNum++).setCellValue(data.getRegistCapi() != null ? data.getRegistCapi() : "");
                row.createCell(colNum++).setCellValue(data.getEconKind() != null ? data.getEconKind() : "");
                row.createCell(colNum++).setCellValue(data.getSignStatus() != null ? data.getSignStatus() : "");
                row.createCell(colNum++).setCellValue(data.getIndustry() != null ? data.getIndustry() : "");
                row.createCell(colNum++).setCellValue(data.getBelong() != null ? data.getBelong() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }


//    导入申诉数据

    @Autowired
    private InputAppealService inputAppealService;

    @Value("${file.upload-dir}"+"\\appeal_file")
    private String uploadDir;


    @PostMapping("/importAppealData")
    public ResponseEntity<ApiResponseDTO<FileMessageDTO>> importAppealData(@RequestParam("file") MultipartFile file) {
        FileMessageDTO fileMessage = new FileMessageDTO();

        try {
            // 1. 保存文件到服务器（非事务操作）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 2. 解析Excel
            AppealExcelReader reader = new AppealExcelReader();
            List<InputAppealDataVO> appeals = reader.readExcel(filePath.toString(), InputAppealDataVO.class);

            if (appeals.isEmpty()) {
                fileMessage.setMessage("文件内容为空");
                fileMessage.setAppealMessage("申诉数据导入失败");
                return ResponseEntity.ok(ApiResponseDTO.success(fileMessage));
            }

            // 3. 数据库操作（事务内）
            inputAppealService.deleteAllAppeal();
            // 3. 清空并导入数据（事务操作）
            for (InputAppealDataVO appeal : appeals) {
                inputAppealService.inputAppeal(appeal);
            }

            // 4. 关键API调用（必须在事务内）
            String AppealMessage = callExternalAppealApi();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(AppealMessage);
            if (rootNode.get("code").asInt() == 200){
                // 5. 全部成功后的响应
                fileMessage.setProcessedCount(appeals.size());
                fileMessage.setMessage("success");
                fileMessage.setAppealMessage("申诉数据推送成功");
            } else {
                fileMessage.setMessage(rootNode.get("msg").asText());
                fileMessage.setAppealMessage("申诉数据推送失败");
            }
            return ResponseEntity.ok(ApiResponseDTO.success(fileMessage));

        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            fileMessage.setMessage("fail");
            fileMessage.setAppealMessage("申诉数据推送失败");
            return ResponseEntity.ok(ApiResponseDTO.error("申诉数据推送失败"));
        } catch (Exception e) {
            // 系统异常
            fileMessage.setMessage("系统处理异常");
            fileMessage.setAppealMessage("申诉数据处理失败");
            return ResponseEntity.ok(ApiResponseDTO.success(fileMessage));
        }

    }


//    调用外部申诉API
//    @return true-成功 false-失败
//     调用外部申诉API
//    @return 返回API的完整JSON响应字符串，失败时返回null

    private String callExternalAppealApi() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://192.168.33.9:8000/appeal_data");

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

