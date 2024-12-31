package com.example.demo.dto;

import lombok.Data;

@Data
public class Person {

    String code;
    String message;
    public Person(String code, String message) {
            this.code = code;
            this.message = message;

        }
}



