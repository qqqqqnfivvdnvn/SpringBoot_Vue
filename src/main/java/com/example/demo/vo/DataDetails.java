package com.example.demo.vo;

import lombok.Data;

@Data
public class DataDetails {
    private int code;
    private String msg;
    private int uncleanedCount;
    private int unappealingCount;
    private int companyCount;
    private int hospitalCount;
    private int drugstoreCount;

}
