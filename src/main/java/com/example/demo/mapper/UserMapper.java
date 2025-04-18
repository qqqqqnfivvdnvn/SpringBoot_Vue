package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper

@DS("master_sqlserver")
public interface UserMapper  extends BaseMapper<User>{

    @Select("SELECT * FROM test..users")
    public List<User> findAll();

    @Select("SELECT top 1 id,username,password,email FROM test..users WHERE username = #{username}")
    public User findByName(String username);

    @Insert("INSERT INTO test..users (id,username, password,email,addtime) VALUES (#{id},#{username},#{password},#{email},#{addtime})")
    public int insert(User user);

    @Delete("DELETE FROM test..users WHERE id = #{id}")
    public int delete(String id);

    @Update("UPDATE test..users SET username = #{username}, password = #{password}, email = #{email} WHERE id = #{id}")
    public int update(User user);




    List<User> findByPage(String name, int offset, int limit);




}


