package com.example.demo.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.HaoSenDataIu;
import com.example.demo.mapper.HaoSenLxDataMapper;
import com.example.demo.service.HaoSenLxDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@DS("slave_pg_liuxiang")

public class HaoSenLxDataServiceImpl extends ServiceImpl<HaoSenLxDataMapper, HaoSenDataIu> implements HaoSenLxDataService {
    @Autowired
    private HaoSenLxDataMapper haoSenLxDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer  lxUpdateData(List<HaoSenDataIu> haoSenDataIuList) {
        try {

            haoSenLxDataMapper.deleteAllIuData();
            if (haoSenDataIuList == null || haoSenDataIuList.size() == 0) {
                return 0;
            }
            boolean result = this.saveOrUpdateBatch(haoSenDataIuList);

            if (result) {
//            执行流向业务库逻辑 新增、更新
                haoSenLxDataMapper.executeAllSQLOperations();
                return haoSenDataIuList.size();

            }

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
