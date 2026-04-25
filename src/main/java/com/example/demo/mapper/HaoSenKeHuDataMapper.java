package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenKeHuDataConditionDTO;
import com.example.demo.entity.HaoSenKeHuData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
@DS("master_sqlserver")
public interface HaoSenKeHuDataMapper {

    // ==================== 名称地址表 ====================

    /**
     * 分页查询名称地址数据
     */
    public List<HaoSenKeHuData> selectNameAddrPageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * 根据 keyid 查询名称地址单条数据
     */
    public HaoSenKeHuData selectNameAddrByKeyid(@Param("keyid") String keyid);

    /**
     * 插入名称地址数据
     */
    public int insertNameAddr(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新名称地址数据
     */
    public int updateNameAddr(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除名称地址数据
     */
    public int deleteNameAddrByKeyid(@Param("keyid") String keyid);


    // ==================== 互联网医院表 ====================

    /**
     * 分页查询互联网医院数据
     */
    public List<HaoSenKeHuData> selectInternetHosPageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * 根据 keyid 查询互联网医院单条数据
     */
    public HaoSenKeHuData selectInternetHosByKeyid(@Param("keyid") String keyid);

    /**
     * 插入互联网医院数据
     */
    public int insertInternetHos(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新互联网医院数据
     */
    public int updateInternetHos(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除互联网医院数据
     */
    public int deleteInternetHosByKeyid(@Param("keyid") String keyid);


    // ==================== 医院大类表 ====================

    /**
     * 分页查询医院大类数据
     */
    public List<HaoSenKeHuData> selectBigClassifyPageList(@Param("condition") HaoSenKeHuDataConditionDTO condition);

    /**
     * 根据 keyid 查询医院大类单条数据
     */
    public HaoSenKeHuData selectBigClassifyByKeyid(@Param("keyid") String keyid);

    /**
     * 插入医院大类数据
     */
    public int insertBigClassify(HaoSenKeHuData haoSenKeHuData);

    /**
     * 更新医院大类数据
     */
    public int updateBigClassify(HaoSenKeHuData haoSenKeHuData);

    /**
     * 删除医院大类数据
     */
    public int deleteBigClassifyByKeyid(@Param("keyid") String keyid);

}
