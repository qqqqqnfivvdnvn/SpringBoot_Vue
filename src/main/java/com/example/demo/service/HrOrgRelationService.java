package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HrOrgRelationConditionDTO;
import com.example.demo.vo.HrOrgRelationVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 恒瑞数据比对关系 Service
 */
public interface HrOrgRelationService {

    ApiResponseDTO<PageInfo<HrOrgRelationVO>> getRelationList(HrOrgRelationConditionDTO condition, int pageNum, int pageSize);

    ApiResponseDTO<String> deleteRelation(String businessLicenseName);

    ApiResponseDTO<String> updateRelation(HrOrgRelationVO relation);

    ApiResponseDTO<String> addRelation(HrOrgRelationVO relation);

    ApiResponseDTO<byte[]> exportRelationExcel(HrOrgRelationConditionDTO condition);
}
