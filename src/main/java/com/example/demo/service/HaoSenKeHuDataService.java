package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenKeHuDataConditionDTO;
import com.example.demo.entity.HaoSenKeHuData;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * 豪森客户数据服务接口
 */
public interface HaoSenKeHuDataService {

    // ==================== 名称地址表 ====================

    /**
     * 分页查询名称地址数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectNameAddrPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据 keyid 查询名称地址单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectNameAddrByKeyid(String keyid);

    /**
     * 插入名称地址数据
     */
    public ApiResponseDTO<Integer> insertNameAddr(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新名称地址数据
     */
    public ApiResponseDTO<Integer> updateNameAddr(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除名称地址数据
     */
    public ApiResponseDTO<Integer> deleteNameAddrByKeyid(String keyid);


    // ==================== 互联网医院表 ====================

    /**
     * 分页查询互联网医院数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectInternetHosPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据 keyid 查询互联网医院单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectInternetHosByKeyid(String keyid);

    /**
     * 插入互联网医院数据
     */
    public ApiResponseDTO<Integer> insertInternetHos(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新互联网医院数据
     */
    public ApiResponseDTO<Integer> updateInternetHos(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除互联网医院数据
     */
    public ApiResponseDTO<Integer> deleteInternetHosByKeyid(String keyid);


    // ==================== 医院大类表 ====================

    /**
     * 分页查询医院大类数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectBigClassifyPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据 keyid 查询医院大类单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectBigClassifyByKeyid(String keyid);

    /**
     * 插入医院大类数据
     */
    public ApiResponseDTO<Integer> insertBigClassify(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新医院大类数据
     */
    public ApiResponseDTO<Integer> updateBigClassify(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除医院大类数据
     */
    public ApiResponseDTO<Integer> deleteBigClassifyByKeyid(String keyid);

    // ==================== 豪森编码表（数据源：slave_pg） ====================

    /**
     * 分页查询豪森编码数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsCodePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据 keyid 查询豪森编码单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectHsCodeByKeyid(String keyid);

    /**
     * 插入豪森编码数据
     */
    public ApiResponseDTO<Integer> insertHsCode(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新豪森编码数据
     */
    public ApiResponseDTO<Integer> updateHsCode(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除豪森编码数据
     */
    public ApiResponseDTO<Integer> deleteHsCodeByKeyid(String keyid);

    // ==================== 总分院编码表（数据源：slave_pg） ====================

    /**
     * 分页查询总分院编码数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHosBranchCodePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据 keyid 查询总分院编码单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectHosBranchCodeByKeyid(String keyid);

    /**
     * 插入总分院编码数据
     */
    public ApiResponseDTO<Integer> insertHosBranchCode(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新总分院编码数据
     */
    public ApiResponseDTO<Integer> updateHosBranchCode(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除总分院编码数据
     */
    public ApiResponseDTO<Integer> deleteHosBranchCodeByKeyid(String keyid);

    // ==================== 豪森线下编码表（数据源：slave_pg） ====================

    /**
     * 分页查询豪森线下编码数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsOfflinePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据原始编码查询豪森线下编码单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectHsOfflineByDataCode(String dataCode);

    /**
     * 插入豪森线下编码数据
     */
    public ApiResponseDTO<Integer> insertHsOffline(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新豪森线下编码数据
     */
    public ApiResponseDTO<Integer> updateHsOffline(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除豪森线下编码数据
     */
    public ApiResponseDTO<Integer> deleteHsOfflineByDataCode(String dataCode);

    // ==================== 豪森医疗联盟 - 医联体编码 ====================

    /**
     * 分页查询医联体编码数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsMedicalAlliancePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据成员单位 keyid 查询医联体编码单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectHsMedicalAllianceByMemberUnitKeyid(String memberUnitKeyid);

    /**
     * 插入医联体编码数据
     */
    public ApiResponseDTO<Integer> insertHsMedicalAlliance(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新医联体编码数据
     */
    public ApiResponseDTO<Integer> updateHsMedicalAlliance(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除医联体编码数据
     */
    public ApiResponseDTO<Integer> deleteHsMedicalAllianceByMemberUnitKeyid(String memberUnitKeyid);

    // ==================== 豪森医疗社区 - 医共体编码 ====================

    /**
     * 分页查询医共体编码数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsMedicalCommunityPageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据成员单位 keyid 查询医共体编码单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectHsMedicalCommunityByMemberUnitKeyid(String memberUnitKeyid);

    /**
     * 插入医共体编码数据
     */
    public ApiResponseDTO<Integer> insertHsMedicalCommunity(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新医共体编码数据
     */
    public ApiResponseDTO<Integer> updateHsMedicalCommunity(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除医共体编码数据
     */
    public ApiResponseDTO<Integer> deleteHsMedicalCommunityByMemberUnitKeyid(String memberUnitKeyid);

    // ==================== 豪森返回编码 - 豪森返回编码 ====================

    /**
     * 分页查询豪森返回编码数据
     */
    public ApiResponseDTO<PageInfo<HaoSenKeHuData>> selectHsBackCodePageList(HaoSenKeHuDataConditionDTO condition, Integer pageNum, Integer pageSize);

    /**
     * 根据原始编码查询豪森返回编码单条数据
     */
    public ApiResponseDTO<HaoSenKeHuData> selectHsBackCodeByDataCode(String dataCode);

}
