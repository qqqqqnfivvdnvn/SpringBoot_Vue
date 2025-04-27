package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.Hospital;
import com.example.demo.vo.HospitalCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
@DS("slave_pg")

public interface MainDataQueryMapper {
    public List<Hospital> HospitalQueryByCondition(@Param("condition") HospitalCondition condition,
                                                   @Param("page") Pageable pageable);

    long countHospitalCondition(@Param("condition") HospitalCondition condition);


}
