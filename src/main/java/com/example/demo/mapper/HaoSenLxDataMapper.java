package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.HaoSenDataIu;
import org.apache.ibatis.annotations.Mapper;

@Mapper

public interface HaoSenLxDataMapper extends BaseMapper<HaoSenDataIu> {

    public  Integer deleteAllIuData();

    public  Integer executeAllSQLOperations();


}
