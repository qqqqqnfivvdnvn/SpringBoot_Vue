package com.example.demo.service.impl;


import com.example.demo.apidata.ApiHaosen;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenFileMessageDTO;
import com.example.demo.entity.HaoSenOrganization;
import com.example.demo.mapper.HaoSenSqlMapper;
import com.example.demo.service.HaoSenUpdateDataService;
import com.example.demo.utils.HaoSenAppealExcelReader;
import com.example.demo.vo.HaoSenAppealDataVO;
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
public class HaoSenUpdateDataServicelmpl implements HaoSenUpdateDataService {


    @Autowired
    private HaoSenSqlMapper sqlMapper;

    @Value("${file.upload-dir}"+"\\update_file")
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

            // 2. 解析Excel
            HaoSenAppealExcelReader reader = new HaoSenAppealExcelReader();
            List<HaoSenInputAppealDataVO> updateData = reader.readExcel(filePath.toString(), HaoSenInputAppealDataVO.class);

            if (updateData.isEmpty()) {
                fileMessage.setMessage("文件内容为空");
                fileMessage.setAppealMessage("更新数据导入失败");
                return ApiResponseDTO.success(fileMessage);
            }

            // 3. 数据库操作（事务内）
            sqlMapper.deleteAllHaoSenData();

            // 3. 清空并导入数据（事务操作）
            for (HaoSenInputAppealDataVO appeal : updateData) {
                sqlMapper.inputHaoSenData(appeal);
            }

            // 4. 关键API调用（必须在事务内）
            String updateMessage = new ApiHaosen().callExternalUpdateApi();


            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(updateMessage);
            if (rootNode.get("code").asInt() == 200){
                // 5. 全部成功后的响应
                fileMessage.setProcessedCount(updateData.size());
                fileMessage.setMessage("success");
                fileMessage.setAppealMessage("更新数据推送成功");
            } else {
                fileMessage.setMessage(rootNode.get("msg").asText());
                fileMessage.setAppealMessage("更新数据推送失败");
            }
            return ApiResponseDTO.success(fileMessage);

        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            fileMessage.setMessage("fail");
            fileMessage.setAppealMessage("更新数据推送失败");
            return ApiResponseDTO.success(fileMessage);
        } catch (Exception e) {
            // 系统异常
            fileMessage.setMessage("系统处理异常");
            fileMessage.setAppealMessage("更新数据处理失败");
            return ApiResponseDTO.success(fileMessage);
        }

    }




    //获取药店、医疗机构大库的单条数据
    @Override
    public ApiResponseDTO<HaoSenAppealDataVO> findDaKuData(String keyid) {
        return ApiResponseDTO.success(sqlMapper.findDaKuData(keyid));
    }



    //更新医院信息，并通过接口推送
    @Override
    public ApiResponseDTO<HaoSenFileMessageDTO> updateOneUpdateData(HaoSenOrganization haoSenOrganization) {
        //给HaoSenAppealDataVO 赋值 ,将 haoSenOrganization 对应的值赋值给HaoSenAppealDataVO

        HaoSenAppealDataVO haoSenUpdateDataVO = new HaoSenAppealDataVO();

        haoSenUpdateDataVO.setBatchCode(haoSenOrganization.getBatchCode());
        haoSenUpdateDataVO.setDataId(haoSenOrganization.getDataId());
        haoSenUpdateDataVO.setDataType(haoSenOrganization.getDataType());
        haoSenUpdateDataVO.setDataCode(haoSenOrganization.getDataCode());
        haoSenUpdateDataVO.setOriginalName(haoSenOrganization.getOriginalName());
        haoSenUpdateDataVO.setOriginalProvince(haoSenOrganization.getOriginalProvince());
        haoSenUpdateDataVO.setOriginalAddress(haoSenOrganization.getOriginalAddress());
        haoSenUpdateDataVO.setCompanyName(haoSenOrganization.getCompanyName());
        haoSenUpdateDataVO.setAppealRemark(haoSenOrganization.getAppealRemark());
        haoSenUpdateDataVO.setSolveRemark(haoSenOrganization.getSolveRemark());
        haoSenUpdateDataVO.setInstitutionType(haoSenOrganization.getOrgType());
        haoSenUpdateDataVO.setKeyid(haoSenOrganization.getKeyid());
        haoSenUpdateDataVO.setName(haoSenOrganization.getName());
        haoSenUpdateDataVO.setNameHistory(haoSenOrganization.getNameHistory());
        haoSenUpdateDataVO.setProvince(haoSenOrganization.getProvince());
        haoSenUpdateDataVO.setProvinceid(haoSenOrganization.getProvinceId());
        haoSenUpdateDataVO.setCity(haoSenOrganization.getCity());
        haoSenUpdateDataVO.setCityid(haoSenOrganization.getCityId());
        haoSenUpdateDataVO.setArea(haoSenOrganization.getArea());
        haoSenUpdateDataVO.setAreaid(haoSenOrganization.getAreaId());
        haoSenUpdateDataVO.setAddress(haoSenOrganization.getAddress());
        haoSenUpdateDataVO.setLevel(haoSenOrganization.getLevel());
        haoSenUpdateDataVO.setGrade(haoSenOrganization.getGrade());
        haoSenUpdateDataVO.setPublicflag(haoSenOrganization.getPublicflag());
        haoSenUpdateDataVO.setClassify(haoSenOrganization.getClassify());
        haoSenUpdateDataVO.setGeneralBranchKid(haoSenOrganization.getGeneralBranchKid());
        haoSenUpdateDataVO.setGeneralBranchName(haoSenOrganization.getGeneralBranchName());
        haoSenUpdateDataVO.setMilitaryHos(haoSenOrganization.getMilitaryHos());
        haoSenUpdateDataVO.setRegcode(haoSenOrganization.getRegcode());
        haoSenUpdateDataVO.setValidity(haoSenOrganization.getValidity());
        haoSenUpdateDataVO.setSubjects(haoSenOrganization.getSubjects());
        haoSenUpdateDataVO.setLegalperson(haoSenOrganization.getLegalperson());
        haoSenUpdateDataVO.setUsci(haoSenOrganization.getUsci());

//        药店字段
//        scope,mainBranchKid,mainBranchName,createDate,registCapi,econKind,signStatus,industry,belong

        haoSenUpdateDataVO.setOperation(haoSenOrganization.getOperation());
        haoSenUpdateDataVO.setScope(haoSenOrganization.getScope());
        haoSenUpdateDataVO.setMainBranchKid(haoSenOrganization.getMainBranchKid());
        haoSenUpdateDataVO.setMainBranchName(haoSenOrganization.getMainBranchName());
        haoSenUpdateDataVO.setCreateDate(haoSenOrganization.getCreateDate());
        haoSenUpdateDataVO.setRegistCapi(haoSenOrganization.getRegistCapi());
        haoSenUpdateDataVO.setEconKind(haoSenOrganization.getEconKind());
        haoSenUpdateDataVO.setSignStatus(haoSenOrganization.getSignStatus());
        haoSenUpdateDataVO.setIndustry(haoSenOrganization.getIndustry());
        haoSenUpdateDataVO.setBelong(haoSenOrganization.getBelong());



        HaoSenFileMessageDTO updateDataMessage = new HaoSenFileMessageDTO();
        ObjectMapper objectMapper = new ObjectMapper();


        try {

            // 1. 数据库操作
            sqlMapper.deleteAllHaoSenData();
            //插入数据
            sqlMapper.inputHaoSenUpdateData(haoSenUpdateDataVO);

//         2. 关键API调用
            String updateMessage = new ApiHaosen().callExternalUpdateApi();
            JsonNode rootNode = objectMapper.readTree(updateMessage);
            if (rootNode.get("code").asInt() == 200){
                // 5. 全部成功后的响应
                updateDataMessage.setProcessedCount(1);
                updateDataMessage.setMessage("success");
                updateDataMessage.setAppealMessage("更新数据推送成功");

            } else {
                updateDataMessage.setMessage(rootNode.get("msg").asText());
                updateDataMessage.setAppealMessage("更新数据推送失败");

            }
            return ApiResponseDTO.success(updateDataMessage);

        } catch (RuntimeException e) {
            // 业务异常（如API调用失败）
            updateDataMessage.setMessage("fail");
            updateDataMessage.setAppealMessage("更新数据推送失败");
            return ApiResponseDTO.success(updateDataMessage);

        } catch (Exception e) {
            // 系统异常
            updateDataMessage.setMessage("fail");
            updateDataMessage.setAppealMessage("更新数据处理失败");
            return ApiResponseDTO.success(updateDataMessage);
        }



    }




}
