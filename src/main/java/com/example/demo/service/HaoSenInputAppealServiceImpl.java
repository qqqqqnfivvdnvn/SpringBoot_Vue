package com.example.demo.service;

import com.example.demo.mapper.HaoSenInputAppealMapper;
import com.example.demo.service.impl.HaoSenInputAppealService;
import com.example.demo.vo.HaoSenInputAppealDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HaoSenInputAppealServiceImpl implements HaoSenInputAppealService {

    @Autowired
    private HaoSenInputAppealMapper inputAppealMapper;


    @Override
    public int inputAppeal(HaoSenInputAppealDataVO inputAppealDataVO) {
        return inputAppealMapper.inputAppeal(inputAppealDataVO);

    }

    @Override
    public int deleteAllAppeal() {
        return inputAppealMapper.deleteAllAppeal();
    }
}
