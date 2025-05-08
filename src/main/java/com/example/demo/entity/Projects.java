package com.example.demo.entity;

import lombok.Data;

@Data
public class Projects {
//    id	name	description	icon	color	owner	updated	is_favorite	type
    private String id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private String owner;
    private String updated;
    private String isFavorite;
    private String type;

}
