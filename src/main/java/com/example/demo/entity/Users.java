package com.example.demo.entity;

//import lombok.Data;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("Users")

public class Users implements Serializable  {

    private String id;
    private String username;
    private String email;
    private String password;
    private String addtime;


}
