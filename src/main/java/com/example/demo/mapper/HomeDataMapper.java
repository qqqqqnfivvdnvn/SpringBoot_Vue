package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import com.example.demo.vo.PieData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DS("slave_pg")
public interface HomeDataMapper {

    public DetailsData getHomeData();

//    获取折现图数据
    public List<BarData> getBarData();

    //实现饼图的数据方法
    public   List<PieData> getPieData();

    //实现玫瑰饼图的数据方法
    public   List<PieData> getRosePieData();
}
