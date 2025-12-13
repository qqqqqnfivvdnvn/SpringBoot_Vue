package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.Projects;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DS("master_sqlserver")
@Mapper
public interface ProjectsMapper {

    public int addProject(Projects projects);

    public int findProject(String name);

    public List<Projects> findAllProjects();

}
