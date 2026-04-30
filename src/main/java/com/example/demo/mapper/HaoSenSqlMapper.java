package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.HaoSenOrganizationVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
@DS("master_sqlserver")
public interface HaoSenSqlMapper {


    public int deleteAllHaoSenData();

    public HaoSenOrganizationVO findDaKuData(String keyid);


    public int inputHaoSenUpdateData(HaoSenOrganizationVO  HaoSenOrganizationVO);

    /**
     * 批量插入临时表（流式读取优化）
     * @param list 数据列表
     * @return 插入条数
     */
    public int batchInputHaoSenUpdateData(List<HaoSenOrganizationVO> list);


}
