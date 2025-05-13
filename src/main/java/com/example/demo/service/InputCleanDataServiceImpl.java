package com.example.demo.service;

import com.example.demo.mapper.CleanDataMapper;
import com.example.demo.service.impl.InputCleanDataService;
import com.example.demo.vo.InputCleanDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InputCleanDataServiceImpl implements InputCleanDataService {

    @Autowired
    private CleanDataMapper cleanDataMapper;

    @Override
    public int inputCleanData(InputCleanDataVO inputCleanDataVO) {
        return cleanDataMapper.inputCleanData(inputCleanDataVO);
    }

    @Override
    public int deleteAllClean() {
        return cleanDataMapper.deleteAllCleanData();
    }


}
