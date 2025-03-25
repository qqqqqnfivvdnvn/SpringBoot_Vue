package com.example.demo.service;

import com.example.demo.dto.Message;
import com.example.demo.dto.ResponseData;
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
    public Message login(User user) {
        User search_user = userMapper.findByName(user.getUsername());
        Message message = new Message();
        ResponseData responseData = new ResponseData();
        // 判断用户名是否存在
        if (search_user == null) {
            message.setCode("300");
            message.setMessage("用户名不存在，请先注册！");
        } else if (!search_user.getPassword().equals(user.getPassword())) {
            message.setCode("301");
            message.setMessage("密码错误！");
        } else {
            message.setCode("200");
            message.setMessage("登录成功！");
            // 生成token
            String token = UUID.randomUUID().toString().replace("-", "");
            responseData.setToken(token);
            responseData.setUsername(search_user.getUsername());
            responseData.setUserId(search_user.getId());
            message.setData(responseData);

        }
        return message;

    }

    @Override
    public Message findByName(User user) {
        User ku_user = userMapper.findByName(user.getUsername());
        Message message = new Message();

        // 判断用户名是否已被注册
        if (ku_user!=null) {
            message.setCode("300");
            message.setMessage("该用户名已被注册！");
            return message;
        }else {
            UUID uuid = UUID.randomUUID();
            user.setId(uuid.toString().replace("-", ""));
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            // 格式化当前时间
            String formattedTime = now.format(formatter);
            user.setAddtime(formattedTime);
            int i = userMapper.insert(user);
            if (i == 0) {
                message.setCode("500");
                message.setMessage("注册失败！");
                return message;
            } else {
                message.setCode("200");
                message.setMessage("注册成功！");
                return message;
            }
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
