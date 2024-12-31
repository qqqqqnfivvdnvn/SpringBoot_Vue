package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;

import com.example.demo.vo.UserMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper  extends BaseMapper<User>{

    @Select("SELECT * FROM test..user_demo")
    public List<User> findAll();

    @Insert("INSERT INTO test..user_demo (id,name, password) VALUES (#{id},#{name},#{password})")
    public int insert(User user);

    @Delete("DELETE FROM test..user_demo WHERE id = #{id}")
    public int delete(String id);

    @Update("UPDATE test..user_demo SET name = #{name}, password = #{password} WHERE id = #{id}")
    public int update(User user);


    List<User> findByPage(String name, int offset, int limit);



    List<UserMessage> joinByPage(String name, int offset, int limit);



}


