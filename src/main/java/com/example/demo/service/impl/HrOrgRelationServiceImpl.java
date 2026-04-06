package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HrOrgRelationConditionDTO;
import com.example.demo.entity.HrOrgRelation;
import com.example.demo.mapper.HrOrgRelationMapper;
import com.example.demo.service.HrOrgRelationService;
import com.example.demo.utils.HrToExcel;
import com.example.demo.vo.HrOrgRelationVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 恒瑞数据比对关系 Service 实现
 */
@Service
public class HrOrgRelationServiceImpl implements HrOrgRelationService {

    @Autowired
    private HrOrgRelationMapper orgRelationMapper;

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
}
