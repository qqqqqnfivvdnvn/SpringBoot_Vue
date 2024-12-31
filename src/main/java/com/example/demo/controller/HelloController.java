package com.example.demo.controller;

import com.example.demo.dto.Person;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/demo")
public class HelloController {
    @RequestMapping(value="/hello", method= RequestMethod.GET)
    public String hello(String id) {
        System.out.println(id);
        return "Hello, World!"+id;
    }

    @PostMapping("/fileupload")
//    localhost:8080/fileupload?name=test
    public String uploadFile(String name, MultipartFile file, HttpServletRequest request) throws IOException {
        System.out.println(name);
        System.out.println(file.getOriginalFilename());
        String  getOriginalFilename=file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String jsonString = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            file.transferTo(new File("F:\\ssss\\"+uuid.toString()+ getOriginalFilename));
            Person person=new Person("200", "上传成功");

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 格式化 JSON
            jsonString = objectMapper.writeValueAsString(person);

        } catch (IOException e) {
            System.out.println("上传失败");
            Person person=new Person("500",e.getMessage());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 格式化 JSON
            jsonString = objectMapper.writeValueAsString(person);

        }


        return jsonString;


    }

    @GetMapping("/swagger")
    public String sayHello() {
        return "Hello, Swagger!";
    }
}
