package com.example.demo.dto;

import lombok.Data;

@Data
public class Message {
    String code;
    String message;
    private ResponseData data;

}
