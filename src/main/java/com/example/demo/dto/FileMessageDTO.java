package com.example.demo.dto;

import lombok.Data;

@Data
public class FileMessageDTO {
    private String message;
    private int processedCount;
    private String appealMessage;

}
