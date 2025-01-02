package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
@DS("slave_pg")
public interface UserPgMapper {

    void insertUser(User user);


}
