package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.dto.AppealConditionDTO;

import com.example.demo.vo.AppealDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
@DS("slave_pg")
public interface AppealDataMapper {
    public List<AppealDataVO> getAppealData(@Param("condition") AppealConditionDTO condition,
                                            @Param("page") Pageable pageable);


    long countAppealCondition(@Param("condition") AppealConditionDTO condition);



}
