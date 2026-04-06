package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.HaoSenUpdateStatusDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@DS("slave_pg")
public interface HaoSenUpdateDataMapper {
    public int updateDrugStoreStatus(@Param("updateStatus") HaoSenUpdateStatusDTO haoSenUpdateStatusDTO);

    public int updateHospitalStatus(@Param("updateStatus") HaoSenUpdateStatusDTO haoSenUpdateStatusDTO);

    public int updateCompanyStatus(@Param("updateStatus") HaoSenUpdateStatusDTO haoSenUpdateStatusDTO);

    public int deleteHospitalDataById(@Param("dataId") String dataId);

    public int deleteDrugStoreDataById(@Param("dataId") String dataId);

    public int deleteCompanyDataById(@Param("dataId") String dataId);

}
