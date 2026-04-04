package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.HaoSenAppealUpdateDataVO;
import com.example.demo.vo.HaoSenBarDataVO;
import com.example.demo.vo.HaoSenDetailsDataVO;
import com.example.demo.vo.PieDataVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DS("slave_pg")
public interface HaoSenHomeDataMapper {

    public HaoSenDetailsDataVO getHomeData();

//    获取柱状图数据
    public List<HaoSenBarDataVO> getCleanBarData();

    //实现饼图的数据方法
    public   PieDataVO getMainPieDataHosClean();

    public   PieDataVO getMainPieDataDrugStore();

    public   PieDataVO getMainPieDataCompany();


    //实现玫瑰饼图的数据方法
    public   PieDataVO getBranchBarDataMainBranch();


    public   PieDataVO getBranchBarDataGeneralBranch();


    //实现折线图的数据方法
    public   List<HaoSenAppealUpdateDataVO> getAppealUpdateData();

}
