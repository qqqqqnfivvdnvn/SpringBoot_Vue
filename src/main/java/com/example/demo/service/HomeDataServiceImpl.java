package com.example.demo.service;

import com.example.demo.mapper.HomeDataMapper;
import com.example.demo.service.impl.HomeDataService;
import com.example.demo.vo.DataDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeDataServiceImpl implements HomeDataService {
    @Autowired
    private HomeDataMapper homeDataMapper;


    @Override
    public DataDetails getDataDetails() {
        try {
            DataDetails dataDetails = homeDataMapper.getHomeData();
            dataDetails.setCode(200);
            dataDetails.setMsg("获取数据成功");
            return dataDetails;
        } catch (Exception e) {
            DataDetails dataDetails = homeDataMapper.getHomeData();
            dataDetails.setCode(500);
            dataDetails.setMsg("获取数据失败");
            return dataDetails;
        }

    }
}
