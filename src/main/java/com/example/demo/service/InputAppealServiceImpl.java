package com.example.demo.service;

import com.example.demo.mapper.InputAppealMapper;
import com.example.demo.service.impl.InputAppealService;
import com.example.demo.vo.InputAppealDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InputAppealServiceImpl implements InputAppealService {

    @Autowired
    private InputAppealMapper inputAppealMapper;


    @Override
    public int inputAppeal(InputAppealDataVO inputAppealDataVO) {
        return inputAppealMapper.inputAppeal(inputAppealDataVO);

    }
}
