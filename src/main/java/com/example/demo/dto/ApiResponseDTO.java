package com.example.demo.dto;

import lombok.Data;

@Data

public class ApiResponseDTO<T> {
    // 基础字段
    private int code;
    private String msg;
    private T data;


    // 手动补充的静态工厂方法（Lombok 不生成这些）
    public static <T> ApiResponseDTO<T> success(T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setCode(200);
        response.setMsg("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String msg) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setCode(500);
        response.setMsg(msg);
        return response;
    }


}
