package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenKeHuDataConditionDTO;
import com.example.demo.entity.HaoSenKeHuData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 豪森客户数据 Mapper（数据源：slave_pg）
 * <p>
 * 负责豪森客户相关数据表的数据库操作，包括：
 * - 豪森编码表 (main_branch_code1121) - 总分店编码
 * - 总分院编码表 (general_branch_code1024)
 * - 豪森线下编码表 (result_hscode)
 * - 豪森医疗联盟表 (haosen_medical_alliance) - 医联体编码
 * - 豪森医疗社区表 (haosen_medical_community) - 医共体编码
 * - 豪森返回编码表 (data_result)
 */
@Mapper
@DS("slave_pg")
public interface HaoSenKeHuPgDataMapper {

    // ==================== 一、豪森编码表 (main_branch_code1121) ====================

    /**
     * [分页查询] 豪森编码列表
     *
     * @param condition 查询条件（keyid/name/hsCode/remarks/时间范围）
     * @return 豪森编码列表
     */
    List<HaoSenKeHuData> selectHsCodePageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * [根据主键查询] 豪森编码单条数据
     *
     * @param keyid 主键 ID
     * @return 豪森编码数据
     */
    HaoSenKeHuData selectHsCodeByKeyid(@Param("keyid") String keyid);

    /**
     * [插入] 豪森编码数据
     *
     * @param haoSenKeHuData 豪森编码数据（keyid/name/hsCode/remarks）
     * @return 影响行数
     */
    int insertHsCode(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [更新] 豪森编码数据
     *
     * @param haoSenKeHuData 豪森编码数据（keyid/name/hsCode/remarks）
     * @return 影响行数
     */
    int updateHsCode(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [删除] 豪森编码数据
     *
     * @param keyid 主键 ID
     * @return 影响行数
     */
    int deleteHsCodeByKeyid(@Param("keyid") String keyid);


    // ==================== 二、总分院编码表 (general_branch_code1024) ====================

    /**
     * [分页查询] 总分院编码列表
     *
     * @param condition 查询条件（keyid/name/hsCode/remarks/时间范围）
     * @return 总分院编码列表
     */
    List<HaoSenKeHuData> selectHosBranchCodePageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * [根据主键查询] 总分院编码单条数据
     *
     * @param keyid 主键 ID
     * @return 总分院编码数据
     */
    HaoSenKeHuData selectHosBranchCodeByKeyid(@Param("keyid") String keyid);

    /**
     * [插入] 总分院编码数据
     *
     * @param haoSenKeHuData 总分院编码数据（keyid/name/hsCode/remarks）
     * @return 影响行数
     */
    int insertHosBranchCode(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [更新] 总分院编码数据
     *
     * @param haoSenKeHuData 总分院编码数据（keyid/name/hsCode/remarks）
     * @return 影响行数
     */
    int updateHosBranchCode(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [删除] 总分院编码数据
     *
     * @param keyid 主键 ID
     * @return 影响行数
     */
    int deleteHosBranchCodeByKeyid(@Param("keyid") String keyid);


    // ==================== 三、豪森线下编码表 (result_hscode) ====================

    /**
     * [分页查询] 豪森线下编码列表
     *
     * @param condition 查询条件（dataCode/originalName/name/hsCode/remark/时间范围）
     * @return 豪森线下编码列表
     */
    List<HaoSenKeHuData> selectHsOfflinePageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * [根据原始编码查询] 豪森线下编码单条数据
     *
     * @param dataCode 原始编码
     * @return 豪森线下编码数据
     */
    HaoSenKeHuData selectHsOfflineByDataCode(@Param("dataCode") String dataCode);

    /**
     * [插入] 豪森线下编码数据
     *
     * @param haoSenKeHuData 豪森线下编码数据（dataCode/originalName/name/hsCode/remark）
     * @return 影响行数
     */
    int insertHsOffline(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [更新] 豪森线下编码数据
     *
     * @param haoSenKeHuData 豪森线下编码数据（dataCode/originalName/name/hsCode/remark）
     * @return 影响行数
     */
    int updateHsOffline(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [删除] 豪森线下编码数据
     *
     * @param dataCode 原始编码（主键）
     * @return 影响行数
     */
    int deleteHsOfflineByDataCode(@Param("dataCode") String dataCode);


    // ==================== 四、豪森医疗联盟表 (haosen_medical_alliance) - 医联体编码 ====================

    /**
     * [分页查询] 医联体编码列表
     *
     * @param condition 查询条件（memberUnit/leadingUnit/cooperationType/province/hsCode/remark/时间范围）
     * @return 医联体编码列表
     */
    List<HaoSenKeHuData> selectHsMedicalAlliancePageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * [根据主键查询] 医联体编码单条数据
     *
     * @param memberUnitKeyid 成员单位 keyid（主键）
     * @return 医联体编码数据
     */
    HaoSenKeHuData selectHsMedicalAllianceByMemberUnitKeyid(@Param("memberUnitKeyid") String memberUnitKeyid);

    /**
     * [插入] 医联体编码数据
     *
     * @param haoSenKeHuData 医联体编码数据（memberUnit/memberUnitKeyid/leadingUnit/leadingUnitKeyid/cooperationType/province/remark/hsCode）
     * @return 影响行数
     */
    int insertHsMedicalAlliance(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [更新] 医联体编码数据
     *
     * @param haoSenKeHuData 医联体编码数据
     * @return 影响行数
     */
    int updateHsMedicalAlliance(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [删除] 医联体编码数据
     *
     * @param memberUnitKeyid 成员单位 keyid（主键）
     * @return 影响行数
     */
    int deleteHsMedicalAllianceByMemberUnitKeyid(@Param("memberUnitKeyid") String memberUnitKeyid);


    // ==================== 五、豪森医疗社区表 (haosen_medical_community) - 医共体编码 ====================

    /**
     * [分页查询] 医共体编码列表
     *
     * @param condition 查询条件（memberUnit/leadingUnit/cooperationType/province/hsCode/remark/时间范围）
     * @return 医共体编码列表
     */
    List<HaoSenKeHuData> selectHsMedicalCommunityPageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * [根据主键查询] 医共体编码单条数据
     *
     * @param memberUnitKeyid 成员单位 keyid（主键）
     * @return 医共体编码数据
     */
    HaoSenKeHuData selectHsMedicalCommunityByMemberUnitKeyid(@Param("memberUnitKeyid") String memberUnitKeyid);

    /**
     * [插入] 医共体编码数据
     *
     * @param haoSenKeHuData 医共体编码数据（memberUnit/memberUnitKeyid/leadingUnit/leadingUnitKeyid/cooperationType/province/remark/hsCode）
     * @return 影响行数
     */
    int insertHsMedicalCommunity(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [更新] 医共体编码数据
     *
     * @param haoSenKeHuData 医共体编码数据
     * @return 影响行数
     */
    int updateHsMedicalCommunity(@Param("data") HaoSenKeHuData haoSenKeHuData);

    /**
     * [删除] 医共体编码数据
     *
     * @param memberUnitKeyid 成员单位 keyid（主键）
     * @return 影响行数
     */
    int deleteHsMedicalCommunityByMemberUnitKeyid(@Param("memberUnitKeyid") String memberUnitKeyid);


    // ==================== 六、豪森返回编码表 (data_result) - 豪森返回编码 ====================

    /**
     * [分页查询] 豪森返回编码列表
     *
     * @param condition 查询条件（feedbackType/batchCode/dataCode/hsCode/status/时间范围）
     * @return 豪森返回编码列表
     */
    List<HaoSenKeHuData> selectHsBackCodePageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * [根据原始编码查询] 豪森返回编码单条数据
     *
     * @param dataCode 原始编码（主键）
     * @return 豪森返回编码数据
     */
    HaoSenKeHuData selectHsBackCodeByDataCode(@Param("dataCode") String dataCode);


    // ==================== 七、总分院店编码批量导入 ====================

    /**
     * [批量插入] 总分院编码（医院）- general_branch_code1024
     * ON CONFLICT DO UPDATE（keyid 为主键，存在则更新）
     *
     * @param list 编码列表（keyid/name/hsCode）
     * @return 影响行数
     */
    int batchInsertHosBranchCode(@Param("list") List<HaoSenKeHuData> list);

    /**
     * [批量插入] 总分店编码（药店）- main_branch_code1121
     * ON CONFLICT DO UPDATE（keyid 为主键，存在则更新）
     *
     * @param list 编码列表（keyid/name/hsCode）
     * @return 影响行数
     */
    int batchInsertDrugBranchCode(@Param("list") List<HaoSenKeHuData> list);

}
