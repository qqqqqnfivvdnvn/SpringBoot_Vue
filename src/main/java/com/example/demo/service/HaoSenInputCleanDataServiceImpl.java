package com.example.demo.service;

import com.example.demo.mapper.HaoSenCleanDataMapper;
import com.example.demo.service.impl.HaoSenInputCleanDataService;
import com.example.demo.vo.HaoSenInputCleanDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HaoSenInputCleanDataServiceImpl implements HaoSenInputCleanDataService {

    @Autowired
    private HaoSenCleanDataMapper cleanDataMapper;

    @Override
    public int inputCleanData(HaoSenInputCleanDataVO inputCleanDataVO) {
        return cleanDataMapper.inputCleanData(inputCleanDataVO);
    }

    @Override
    public int deleteAllClean() {
        return cleanDataMapper.deleteAllCleanData();
    }


}
