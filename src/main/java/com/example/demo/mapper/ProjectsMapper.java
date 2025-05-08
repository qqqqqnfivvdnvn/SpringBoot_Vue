package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.Projects;

@DS("master_sqlserver")

public interface ProjectsMapper {

    public int addProject(Projects projects);

    public int findProject(String name);


}
