package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Users;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper

@DS("master_sqlserver")
public interface UserMapper  extends BaseMapper<Users>{

    @Select("SELECT id,username, password,email,addtime FROM haosen_project..users")
    public List<Users> findAll();

    @Select("SELECT top 1 id,username,password,email FROM haosen_project..users WHERE username = #{username}")
    public Users findByName(String username);

    @Insert("INSERT INTO haosen_project..users (id,username, password,email,addtime) VALUES (#{id},#{username},#{password},#{email},#{addtime})")
    public int insert(Users user);

    @Delete("DELETE FROM haosen_project..users WHERE id = #{id}")
    public int delete(String id);

    @Update("UPDATE test..haosen_project SET username = #{username}, password = #{password}, email = #{email} WHERE id = #{id}")
    public int update(Users user);


    List<Users> findByPage(String name, int offset, int limit);




}


