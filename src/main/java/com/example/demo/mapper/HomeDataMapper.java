package com.example.demo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.vo.DataDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("slave_pg")
public interface HomeDataMapper {
    @Select("SELECT " +
            "(SELECT count(*) from origin_data WHERE batch_code in " +
            "(SELECT batch_code from data_batch WHERE status=1)) as uncleanedcount, " +
            "(SELECT count(*) from data_result WHERE feedback_type='1' and (status is null or status='1')) as unappealingcount, " +
            "(SELECT count(*) from base_org_company WHERE status in (1,3,4)) as companyCount, " +
            "(SELECT count(*) from base_org_hos_clean WHERE status in (1,3,4)) as hospitalCount, " +
            "(SELECT count(*) from base_org_drug_store WHERE status in (1,3,4)) as drugstorecount")

    public DataDetails getHomeData();

}
