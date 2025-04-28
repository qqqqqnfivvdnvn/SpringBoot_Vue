package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.AppealUpdateDataVO;
import com.example.demo.vo.BarDataVO;
import com.example.demo.vo.DetailsDataVO;
import com.example.demo.vo.PieDataVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DS("slave_pg")
public interface HomeDataMapper {

    public DetailsDataVO getHomeData();

//    获取柱状图数据
    public List<BarDataVO> getCleanBarData();

    //实现饼图的数据方法
    public   List<PieDataVO> getMainPieData();

    //实现玫瑰饼图的数据方法
    public   List<PieDataVO> getBranchBarData();

    //实现折线图的数据方法
    public   List<AppealUpdateDataVO> getAppealUpdateData();

}
