package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.AppealUpdateData;
import com.example.demo.vo.BarData;
import com.example.demo.vo.DetailsData;
import com.example.demo.vo.PieData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DS("slave_pg")
public interface HomeDataMapper {

    public DetailsData getHomeData();

//    获取柱状图数据
    public List<BarData> getCleanBarData();

    //实现饼图的数据方法
    public   List<PieData> getMainPieData();

    //实现玫瑰饼图的数据方法
    public   List<PieData> getBranchBarData();

    //实现折线图的数据方法
    public   List<AppealUpdateData> getAppealUpdateData();

}
