package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * (UserInfo)实体类
 *
 * @author makejava
 * @since 2024-12-29 23:18:15
 */
@Data
@TableName("user_info")
public class UserInfo implements Serializable {

    private String id;
    private String age;

    private String gender;

    private String phone;


}

