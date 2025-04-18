package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.vo.UserLoginData;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserMapper userMapper;


    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public ApiResponse<UserLoginData> login(User user) {
        User searchUser = userMapper.findByName(user.getUsername());

        UserLoginData responseData = new UserLoginData();

        // 判断用户名是否存在
        if (searchUser == null) {
            return ApiResponse.error("用户名不存在！");
        }

        if (!searchUser.getPassword().equals(user.getPassword())) {
            return ApiResponse.error("密码错误！");
        }

        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        responseData.setToken(token);
        responseData.setUsername(searchUser.getUsername());
        responseData.setUserid(searchUser.getId());
        ApiResponse.success(responseData);


        return ApiResponse.success(responseData);

    }

    @Override
    public ApiResponse<User> findByName(User user) {
        User kuUser = userMapper.findByName(user.getUsername());

        // 判断用户名是否已被注册
        if (kuUser != null) {
            return ApiResponse.error("该用户名已被注册!");
        }
        // 注册成功
        UUID uuid = UUID.randomUUID();
        user.setId(uuid.toString().replace("-", ""));
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // 格式化当前时间
        String formattedTime = now.format(formatter);
        user.setAddtime(formattedTime);

        int i = userMapper.insert(user);
        if (i == 0) {
            return ApiResponse.error("注册失败！");
        } else {
            return ApiResponse.success(user);
        }

    }

    @Override
    public int delete(String id) {
        return userMapper.delete(id);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public List<User> getUsersByPage(String name, int page, int limit) {
        int offset = (page - 1) * limit;
        return userMapper.findByPage(name, offset, limit);
    }




}
