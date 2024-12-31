package com.example.demo.entity;

//import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName("user_demo")

public class User  implements Serializable  {

    private String id;

    private String name;
    private String password;


}
