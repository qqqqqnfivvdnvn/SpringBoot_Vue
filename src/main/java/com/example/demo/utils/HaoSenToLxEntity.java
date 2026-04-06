package com.example.demo.utils;

import com.example.demo.entity.HaoSenDataIu;
import com.example.demo.vo.HaoSenOrganizationVO;


public class HaoSenToLxEntity {

    public HaoSenDataIu ToLxColumn(HaoSenOrganizationVO haoSenOrganizationVO ){
        HaoSenDataIu haoSenDataIu = new HaoSenDataIu();
        haoSenDataIu.setDataId(haoSenOrganizationVO.getDataId());
        haoSenDataIu.setKeyid(haoSenOrganizationVO.getKeyid());
        haoSenDataIu.setAppealRemark(haoSenOrganizationVO.getAppealRemark());
        haoSenDataIu.setDistributor(haoSenOrganizationVO.getCompanyName());
        haoSenDataIu.setOriginalDataName(haoSenOrganizationVO.getOriginalName());
        haoSenDataIu.setOriginalProvince(haoSenOrganizationVO.getOriginalProvince());
        haoSenDataIu.setOriginalDataAddress(haoSenOrganizationVO.getOriginalAddress());
        haoSenDataIu.setOrgName(haoSenOrganizationVO.getName());
        haoSenDataIu.setHistoryName(haoSenOrganizationVO.getNameHistory());
        haoSenDataIu.setProvince(haoSenOrganizationVO.getProvince());
        haoSenDataIu.setProvinceId(haoSenOrganizationVO.getProvinceId());
        haoSenDataIu.setCity(haoSenOrganizationVO.getCity());
        haoSenDataIu.setCityId(haoSenOrganizationVO.getCityId());
        haoSenDataIu.setAreaName(haoSenOrganizationVO.getArea());
        haoSenDataIu.setAreaId(haoSenOrganizationVO.getAreaId());
        haoSenDataIu.setAddress(haoSenOrganizationVO.getAddress());
        haoSenDataIu.setOrgLevel(haoSenOrganizationVO.getLevel());
        haoSenDataIu.setOrgGrade(haoSenOrganizationVO.getGrade());
        haoSenDataIu.setOrgOwnerForm(haoSenOrganizationVO.getPublicflag());
        haoSenDataIu.setOrgFiveProperty(haoSenOrganizationVO.getClassify());
        haoSenDataIu.setSuperiorUnitKid(haoSenOrganizationVO.getGeneralBranchKid());
        haoSenDataIu.setBranchKid(haoSenOrganizationVO.getMainBranchKid());
        haoSenDataIu.setIsPla(haoSenOrganizationVO.getMilitaryHos());
        haoSenDataIu.setRegCode(haoSenOrganizationVO.getRegcode());
        haoSenDataIu.setValidity(haoSenOrganizationVO.getValidity());
        haoSenDataIu.setMedicalSubjects(haoSenOrganizationVO.getSubjects());
        haoSenDataIu.setOrgLegalPerson(haoSenOrganizationVO.getLegalperson());
        haoSenDataIu.setCreditCode(haoSenOrganizationVO.getUsci());
        haoSenDataIu.setBusinessModel(haoSenOrganizationVO.getOperation());
        haoSenDataIu.setScope(haoSenOrganizationVO.getScope());
        haoSenDataIu.setCreateDate(haoSenOrganizationVO.getCreateDate());
        haoSenDataIu.setRegistCapi(haoSenOrganizationVO.getRegistCapi());
        haoSenDataIu.setEconKind(haoSenOrganizationVO.getEconKind());
        haoSenDataIu.setRegStatus(haoSenOrganizationVO.getSignStatus());
        haoSenDataIu.setIndustry(haoSenOrganizationVO.getIndustry());
        haoSenDataIu.setApprovaOrg(haoSenOrganizationVO.getBelong());
        haoSenDataIu.setSuperiorUnitName(haoSenOrganizationVO.getGeneralBranch());
        haoSenDataIu.setBranchName(haoSenOrganizationVO.getMainBranch());
        haoSenDataIu.setBaseTypeName(haoSenOrganizationVO.getOrgType());

        return haoSenDataIu;

    }

}
