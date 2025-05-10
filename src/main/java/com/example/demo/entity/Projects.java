package com.example.demo.entity;

import lombok.Data;

@Data
public class Projects {
//    id	name	description	icon	color	updated	is_favorite	type	route_name	addtime	user_id
    private String id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private String routeName;
    private String addtime;
    private String userId;
    private String userName;


}
