package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenKeHuDataConditionDTO;
import com.example.demo.entity.HaoSenKeHuData;
import com.example.demo.mapper.HaoSenKeHuDataMapper;
import com.example.demo.mapper.HaoSenKeHuPgDataMapper;
import com.example.demo.service.HaoSenKeHuDataService;
import com.example.demo.utils.ExcelHeaderValidator;
import com.example.demo.utils.ReaderExcel;
import com.example.demo.vo.BranchCodeImportVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 豪森客户数据服务实现类
 */
@Service
public class HaoSenKeHuDataServiceImpl implements HaoSenKeHuDataService {

    @Autowired
    private HaoSenKeHuDataMapper haoSenKeHuDataMapper;

    @Autowired


    private HaoSenKeHuPgDataMapper haoSenKeHuPgDataMapper;

    // ==================== 名称地址表 ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectNameAddrPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuDataMapper.selectNameAddrPageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectNameAddrByKeyid(String keyid) {
        try {
            HaoSenKeHuData data = haoSenKeHuDataMapper.selectNameAddrByKeyid(keyid);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertNameAddr(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuDataMapper.insertNameAddr(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateNameAddr(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getKeyid() == null || haoSenKeHuData.getKeyid().isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuDataMapper.updateNameAddr(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteNameAddrByKeyid(String keyid) {
        try {
            if (keyid == null || keyid.isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuDataMapper.deleteNameAddrByKeyid(keyid);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }


    // ==================== 互联网医院表 ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectInternetHosPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuDataMapper.selectInternetHosPageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectInternetHosByKeyid(String keyid) {
        try {
            HaoSenKeHuData data = haoSenKeHuDataMapper.selectInternetHosByKeyid(keyid);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertInternetHos(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuDataMapper.insertInternetHos(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateInternetHos(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getKeyid() == null || haoSenKeHuData.getKeyid().isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuDataMapper.updateInternetHos(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteInternetHosByKeyid(String keyid) {
        try {
            if (keyid == null || keyid.isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuDataMapper.deleteInternetHosByKeyid(keyid);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }


    // ==================== 医院大类表 ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectBigClassifyPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuDataMapper.selectBigClassifyPageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectBigClassifyByKeyid(String keyid) {
        try {
            HaoSenKeHuData data = haoSenKeHuDataMapper.selectBigClassifyByKeyid(keyid);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertBigClassify(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuDataMapper.insertBigClassify(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateBigClassify(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getKeyid() == null || haoSenKeHuData.getKeyid().isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuDataMapper.updateBigClassify(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteBigClassifyByKeyid(String keyid) {
        try {
            if (keyid == null || keyid.isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuDataMapper.deleteBigClassifyByKeyid(keyid);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 豪森编码表（数据源：slave_pg） ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsCodePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuPgDataMapper.selectHsCodePageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectHsCodeByKeyid(String keyid) {
        try {
            HaoSenKeHuData data = haoSenKeHuPgDataMapper.selectHsCodeByKeyid(keyid);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertHsCode(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuPgDataMapper.insertHsCode(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateHsCode(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getKeyid() == null || haoSenKeHuData.getKeyid().isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.updateHsCode(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteHsCodeByKeyid(String keyid) {
        try {
            if (keyid == null || keyid.isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.deleteHsCodeByKeyid(keyid);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 总分院编码表（数据源：slave_pg） ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHosBranchCodePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuPgDataMapper.selectHosBranchCodePageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectHosBranchCodeByKeyid(String keyid) {
        try {
            HaoSenKeHuData data = haoSenKeHuPgDataMapper.selectHosBranchCodeByKeyid(keyid);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertHosBranchCode(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuPgDataMapper.insertHosBranchCode(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateHosBranchCode(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getKeyid() == null || haoSenKeHuData.getKeyid().isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.updateHosBranchCode(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteHosBranchCodeByKeyid(String keyid) {
        try {
            if (keyid == null || keyid.isEmpty()) {
                return ApiResponseDTO.error("keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.deleteHosBranchCodeByKeyid(keyid);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 豪森线下编码表（数据源：slave_pg） ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsOfflinePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuPgDataMapper.selectHsOfflinePageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectHsOfflineByDataCode(String dataCode) {
        try {
            HaoSenKeHuData data = haoSenKeHuPgDataMapper.selectHsOfflineByDataCode(dataCode);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertHsOffline(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuPgDataMapper.insertHsOffline(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateHsOffline(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getDataCode() == null || haoSenKeHuData.getDataCode().isEmpty()) {
                return ApiResponseDTO.error("原始编码不能为空");
            }

            int result = haoSenKeHuPgDataMapper.updateHsOffline(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteHsOfflineByDataCode(String dataCode) {
        try {
            if (dataCode == null || dataCode.isEmpty()) {
                return ApiResponseDTO.error("原始编码不能为空");
            }

            int result = haoSenKeHuPgDataMapper.deleteHsOfflineByDataCode(dataCode);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 豪森医疗联盟 - 医联体编码 ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsMedicalAlliancePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuPgDataMapper.selectHsMedicalAlliancePageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectHsMedicalAllianceByMemberUnitKeyid(String memberUnitKeyid) {
        try {
            HaoSenKeHuData data = haoSenKeHuPgDataMapper.selectHsMedicalAllianceByMemberUnitKeyid(memberUnitKeyid);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertHsMedicalAlliance(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuPgDataMapper.insertHsMedicalAlliance(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateHsMedicalAlliance(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getMemberUnitKeyid() == null || haoSenKeHuData.getMemberUnitKeyid().isEmpty()) {
                return ApiResponseDTO.error("成员单位 keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.updateHsMedicalAlliance(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteHsMedicalAllianceByMemberUnitKeyid(String memberUnitKeyid) {
        try {
            if (memberUnitKeyid == null || memberUnitKeyid.isEmpty()) {
                return ApiResponseDTO.error("成员单位 keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.deleteHsMedicalAllianceByMemberUnitKeyid(memberUnitKeyid);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 豪森医疗社区 - 医共体编码 ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsMedicalCommunityPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuPgDataMapper.selectHsMedicalCommunityPageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectHsMedicalCommunityByMemberUnitKeyid(String memberUnitKeyid) {
        try {
            HaoSenKeHuData data = haoSenKeHuPgDataMapper.selectHsMedicalCommunityByMemberUnitKeyid(memberUnitKeyid);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> insertHsMedicalCommunity(HaoSenKeHuData haoSenKeHuData) {
        try {
            int result = haoSenKeHuPgDataMapper.insertHsMedicalCommunity(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("插入失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> updateHsMedicalCommunity(HaoSenKeHuData haoSenKeHuData) {
        try {
            if (haoSenKeHuData.getMemberUnitKeyid() == null || haoSenKeHuData.getMemberUnitKeyid().isEmpty()) {
                return ApiResponseDTO.error("成员单位 keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.updateHsMedicalCommunity(haoSenKeHuData);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponseDTO<Integer> deleteHsMedicalCommunityByMemberUnitKeyid(String memberUnitKeyid) {
        try {
            if (memberUnitKeyid == null || memberUnitKeyid.isEmpty()) {
                return ApiResponseDTO.error("成员单位 keyid 不能为空");
            }

            int result = haoSenKeHuPgDataMapper.deleteHsMedicalCommunityByMemberUnitKeyid(memberUnitKeyid);
            if (result > 0) {
                return ApiResponseDTO.success(result);
            } else {
                return ApiResponseDTO.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 豪森返回编码 ====================

    @Override
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsBackCodePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 20);
            List<HaoSenKeHuData> list = haoSenKeHuPgDataMapper.selectHsBackCodePageList(condition);
            return ApiResponseDTO.success(new PageInfo<>(list));
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiResponseDTO<HaoSenKeHuData> selectHsBackCodeByDataCode(String dataCode) {
        try {
            HaoSenKeHuData data = haoSenKeHuPgDataMapper.selectHsBackCodeByDataCode(dataCode);
            if (data != null) {
                return ApiResponseDTO.success(data);
            } else {
                return ApiResponseDTO.error("未找到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("查询失败：" + e.getMessage());
        }
    }

    // ==================== 总分院店编码导入 ====================
    // 总分院店编码导入文件路径（从 application.properties 读取基础路径）
    @Value("${file.upload-dir}" + "/branch_code_file")
    private String uploadDir;
    @Override
    public ApiResponseDTO<Map<String, Integer>> importBranchCodeExcel(org.springframework.web.multipart.MultipartFile file) {
        try {
            // 1. 校验文件
            if (file == null || file.isEmpty()) {
                return ApiResponseDTO.error("文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
                return ApiResponseDTO.error("文件格式不支持，请上传 Excel 文件（.xlsx 或 .xls）");
            }

            // 2. 保存文件到服务器（使用 Files.copy 避免相对路径问题）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String newFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 3. 验证表头
            ExcelHeaderValidator.HeaderValidationResult headerResult =
                    ExcelHeaderValidator.validate(filePath.toString(), BranchCodeImportVO.EXPECTED_HEADERS);

            if (!headerResult.isValid()) {
                String errorMsg = headerResult.getMessage() != null
                        ? headerResult.getMessage() : "表头验证失败，请检查 Excel 表头格式";
                return ApiResponseDTO.error(errorMsg);
            }

            // 4. 流式读取 Excel，按机构类型分组
            ReaderExcel readerExcel = new ReaderExcel();
            List<HaoSenKeHuData> hosList = new ArrayList<>();  // 医院列表
            List<HaoSenKeHuData> drugList = new ArrayList<>(); // 药店列表
            AtomicInteger skipCount = new AtomicInteger(0);    // 跳过（无效机构类型）

            readerExcel.readExcelStreaming(filePath.toString(), BranchCodeImportVO.class, batch -> {
                for (BranchCodeImportVO vo : batch) {
                    // 校验必填字段
                    if (vo.getKeyid() == null || vo.getKeyid().trim().isEmpty()
                            || vo.getName() == null || vo.getName().trim().isEmpty()) {
                        skipCount.incrementAndGet();
                        continue;
                    }

                    HaoSenKeHuData entity = new HaoSenKeHuData();
                    entity.setKeyid(vo.getKeyid().trim());
                    entity.setName(vo.getName().trim());
                    entity.setHsCode(vo.getHsCode() != null ? vo.getHsCode().trim() : null);

                    // 根据机构类型分类
                    String orgType = vo.getOrgType() != null ? vo.getOrgType().trim() : "";
                    if ("医院".equals(orgType)) {
                        hosList.add(entity);
                    } else if ("药店".equals(orgType)) {
                        drugList.add(entity);
                    } else {
                        skipCount.incrementAndGet();
                    }
                }
            }, ReaderExcel.BATCH_SIZE_5_FIELDS);

            // 5. 批量插入数据库（使用 ON CONFLICT DO UPDATE）
            int hosCount = 0;
            int drugCount = 0;

            // 每批 200 条分批插入
            if (!hosList.isEmpty()) {
                for (int i = 0; i < hosList.size(); i += 200) {
                    List<HaoSenKeHuData> subList = hosList.subList(i, Math.min(i + 200, hosList.size()));
                    hosCount += haoSenKeHuPgDataMapper.batchInsertHosBranchCode(subList);
                }
            }

            if (!drugList.isEmpty()) {
                for (int i = 0; i < drugList.size(); i += 200) {
                    List<HaoSenKeHuData> subList = drugList.subList(i, Math.min(i + 200, drugList.size()));
                    drugCount += haoSenKeHuPgDataMapper.batchInsertDrugBranchCode(subList);
                }
            }

            // 6. 返回结果
            Map<String, Integer> result = new HashMap<>();
            result.put("hosCount", hosList.size());
            result.put("drugCount", drugList.size());
            result.put("skipCount", skipCount.get());
            result.put("total", hosList.size() + drugList.size());

            return ApiResponseDTO.success(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("导入失败：" + e.getMessage());
        }
    }
}
