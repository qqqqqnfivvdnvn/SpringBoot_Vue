package com.example.demo.dto;

import lombok.Data;

@Data

public class ApiResponse<T> {
    // 基础字段
    private int code;
    private String msg;
    private T data;


    // 手动补充的静态工厂方法（Lombok 不生成这些）
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMsg("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String msg) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(500);
        response.setMsg(msg);
        return response;
    }


}
