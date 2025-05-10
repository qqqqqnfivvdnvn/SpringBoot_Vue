package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.InputAppealDataVO;
import org.apache.ibatis.annotations.Mapper;

@DS("master_sqlserver")
@Mapper
public interface InputAppealMapper {

    public int inputAppeal(InputAppealDataVO inputAppealDataVO );


}
