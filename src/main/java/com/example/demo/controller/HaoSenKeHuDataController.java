package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.HaoSenKeHuDataConditionDTO;
import com.example.demo.entity.HaoSenKeHuData;
import com.example.demo.service.HaoSenKeHuDataService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 豪森客户数据控制器
 */
@Controller
@RequestMapping("/haosen/kehu")
public class HaoSenKeHuDataController {

    @Autowired
    private HaoSenKeHuDataService haoSenKeHuDataService;

    // ==================== 名称地址表 ====================

    /**
     * 分页查询名称地址数据
     */
    @GetMapping("/nameaddr/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectNameAddrPageList(condition, pageNum, pageSize));
    }

    /**
     * 根据 keyid 查询单条数据
     */
    @GetMapping("/nameaddr/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getDetail(@RequestParam("keyid") String keyid) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectNameAddrByKeyid(keyid));
    }

    /**
     * 插入数据
     */
    @PostMapping("/nameaddr/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insert(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertNameAddr(haoSenKeHuData));
    }

    /**
     * 更新数据
     */
    @PostMapping("/nameaddr/update")
    public ResponseEntity<ApiResponseDTO<Integer>> update(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateNameAddr(haoSenKeHuData));
    }

    /**
     * 删除数据
     */
    @PostMapping("/nameaddr/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> delete(@RequestBody Map<String, String> params) {
        String keyid = params.get("keyid");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteNameAddrByKeyid(keyid));
    }


    // ==================== 互联网医院表 ====================

    /**
     * 分页查询互联网医院数据
     */
    @GetMapping("/internethos/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getInternetHosList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectInternetHosPageList(condition, pageNum, pageSize));
    }

    /**
     * 根据 keyid 查询互联网医院单条数据
     */
    @GetMapping("/internethos/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getInternetHosDetail(@RequestParam("keyid") String keyid) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectInternetHosByKeyid(keyid));
    }

    /**
     * 插入互联网医院数据
     */
    @PostMapping("/internethos/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insertInternetHos(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertInternetHos(haoSenKeHuData));
    }

    /**
     * 更新互联网医院数据
     */
    @PostMapping("/internethos/update")
    public ResponseEntity<ApiResponseDTO<Integer>> updateInternetHos(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateInternetHos(haoSenKeHuData));
    }

    /**
     * 删除互联网医院数据
     */
    @PostMapping("/internethos/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteInternetHos(@RequestBody Map<String, String> params) {
        String keyid = params.get("keyid");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteInternetHosByKeyid(keyid));
    }


    // ==================== 医院大类表 ====================

    /**
     * 分页查询医院大类数据
     */
    @GetMapping("/bigclassify/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getBigClassifyList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectBigClassifyPageList(condition, pageNum, pageSize));
    }

    /**
     * 根据 keyid 查询医院大类单条数据
     */
    @GetMapping("/bigclassify/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getBigClassifyDetail(@RequestParam("keyid") String keyid) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectBigClassifyByKeyid(keyid));
    }

    /**
     * 插入医院大类数据
     */
    @PostMapping("/bigclassify/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insertBigClassify(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertBigClassify(haoSenKeHuData));
    }

    /**
     * 更新医院大类数据
     */
    @PostMapping("/bigclassify/update")
    public ResponseEntity<ApiResponseDTO<Integer>> updateBigClassify(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateBigClassify(haoSenKeHuData));
    }

    /**
     * 删除医院大类数据
     */
    @PostMapping("/bigclassify/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteBigClassify(@RequestBody Map<String, String> params) {
        String keyid = params.get("keyid");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteBigClassifyByKeyid(keyid));
    }

    // ==================== 总分店编码表（数据源：slave_pg） ====================

    /**
     * 分页查询总分店编码数据
     */
    @GetMapping("/drugbranchcode/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getDrugBranchCodeList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsCodePageList(condition, pageNum, pageSize));
    }

    /**
     * 根据 keyid 查询总分店编码单条数据
     */
    @GetMapping("/drugbranchcode/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getDrugBranchCodeDetail(@RequestParam("keyid") String keyid) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsCodeByKeyid(keyid));
    }

    /**
     * 插入总分店编码数据
     */
    @PostMapping("/drugbranchcode/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insertDrugBranchCode(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertHsCode(haoSenKeHuData));
    }

    /**
     * 更新总分店编码数据
     */
    @PostMapping("/drugbranchcode/update")
    public ResponseEntity<ApiResponseDTO<Integer>> updateDrugBranchCode(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateHsCode(haoSenKeHuData));
    }

    /**
     * 删除总分店编码数据
     */
    @PostMapping("/drugbranchcode/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteDrugBranchCode(@RequestBody Map<String, String> params) {
        String keyid = params.get("keyid");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteHsCodeByKeyid(keyid));
    }

    // ==================== 总分院编码表（数据源：slave_pg） ====================

    /**
     * 分页查询总分院编码数据
     */
    @GetMapping("/hosbranchcode/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getHosBranchCodeList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHosBranchCodePageList(condition, pageNum, pageSize));
    }

    /**
     * 根据 keyid 查询总分院编码单条数据
     */
    @GetMapping("/hosbranchcode/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getHosBranchCodeDetail(@RequestParam("keyid") String keyid) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHosBranchCodeByKeyid(keyid));
    }

    /**
     * 插入总分院编码数据
     */
    @PostMapping("/hosbranchcode/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insertHosBranchCode(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertHosBranchCode(haoSenKeHuData));
    }

    /**
     * 更新总分院编码数据
     */
    @PostMapping("/hosbranchcode/update")
    public ResponseEntity<ApiResponseDTO<Integer>> updateHosBranchCode(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateHosBranchCode(haoSenKeHuData));
    }

    /**
     * 删除总分院编码数据
     */
    @PostMapping("/hosbranchcode/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteHosBranchCode(@RequestBody Map<String, String> params) {
        String keyid = params.get("keyid");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteHosBranchCodeByKeyid(keyid));
    }

    // ==================== 豪森线下编码表（数据源：slave_pg） ====================

    /**
     * 分页查询豪森线下编码数据
     */
    @GetMapping("/hscode/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getHsOfflineList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsOfflinePageList(condition, pageNum, pageSize));
    }

    /**
     * 根据原始编码查询豪森线下编码单条数据
     */
    @GetMapping("/hscode/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getHsOfflineDetail(@RequestParam("dataCode") String dataCode) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsOfflineByDataCode(dataCode));
    }

    /**
     * 插入豪森线下编码数据
     */
    @PostMapping("/hscode/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insertHsOffline(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertHsOffline(haoSenKeHuData));
    }

    /**
     * 更新豪森线下编码数据
     */
    @PostMapping("/hscode/update")
    public ResponseEntity<ApiResponseDTO<Integer>> updateHsOffline(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateHsOffline(haoSenKeHuData));
    }

    /**
     * 删除豪森线下编码数据
     */
    @PostMapping("/hscode/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteHsOffline(@RequestBody Map<String, String> params) {
        String dataCode = params.get("dataCode");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteHsOfflineByDataCode(dataCode));
    }

    // ==================== 豪森医疗联盟表（数据源：slave_pg）- 医联体编码 ====================

    /**
     * 分页查询豪森医疗联盟数据
     */
    @GetMapping("/hsmedicalalliance/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getHsMedicalAllianceList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsMedicalAlliancePageList(condition, pageNum, pageSize));
    }

    /**
     * 根据成员单位 keyid 查询豪森医疗联盟单条数据
     */
    @GetMapping("/hsmedicalalliance/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getHsMedicalAllianceDetail(@RequestParam("memberUnitKeyid") String memberUnitKeyid) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsMedicalAllianceByMemberUnitKeyid(memberUnitKeyid));
    }

    /**
     * 插入豪森医疗联盟数据
     */
    @PostMapping("/hsmedicalalliance/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insertHsMedicalAlliance(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertHsMedicalAlliance(haoSenKeHuData));
    }

    /**
     * 更新豪森医疗联盟数据
     */
    @PostMapping("/hsmedicalalliance/update")
    public ResponseEntity<ApiResponseDTO<Integer>> updateHsMedicalAlliance(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateHsMedicalAlliance(haoSenKeHuData));
    }

    /**
     * 删除豪森医疗联盟数据
     */
    @PostMapping("/hsmedicalalliance/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteHsMedicalAlliance(@RequestBody Map<String, String> params) {
        String memberUnitKeyid = params.get("memberUnitKeyid");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteHsMedicalAllianceByMemberUnitKeyid(memberUnitKeyid));
    }

    // ==================== 豪森医疗社区表（数据源：slave_pg）- 医共体编码 ====================

    /**
     * 分页查询医共体编码数据
     */
    @GetMapping("/hsmedicalcommunity/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getHsMedicalCommunityList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsMedicalCommunityPageList(condition, pageNum, pageSize));
    }

    /**
     * 根据成员单位 keyid 查询医共体编码单条数据
     */
    @GetMapping("/hsmedicalcommunity/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getHsMedicalCommunityDetail(@RequestParam("memberUnitKeyid") String memberUnitKeyid) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsMedicalCommunityByMemberUnitKeyid(memberUnitKeyid));
    }

    /**
     * 插入医共体编码数据
     */
    @PostMapping("/hsmedicalcommunity/insert")
    public ResponseEntity<ApiResponseDTO<Integer>> insertHsMedicalCommunity(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.insertHsMedicalCommunity(haoSenKeHuData));
    }

    /**
     * 更新医共体编码数据
     */
    @PostMapping("/hsmedicalcommunity/update")
    public ResponseEntity<ApiResponseDTO<Integer>> updateHsMedicalCommunity(@RequestBody HaoSenKeHuData haoSenKeHuData) {
        return ResponseEntity.ok(haoSenKeHuDataService.updateHsMedicalCommunity(haoSenKeHuData));
    }

    /**
     * 删除医共体编码数据
     */
    @PostMapping("/hsmedicalcommunity/delete")
    public ResponseEntity<ApiResponseDTO<Integer>> deleteHsMedicalCommunity(@RequestBody Map<String, String> params) {
        String memberUnitKeyid = params.get("memberUnitKeyid");
        return ResponseEntity.ok(haoSenKeHuDataService.deleteHsMedicalCommunityByMemberUnitKeyid(memberUnitKeyid));
    }

    // ==================== 豪森返回编码表（数据源：slave_pg）- 豪森返回编码 ====================

    /**
     * 分页查询豪森返回编码数据
     */
    @GetMapping("/backhscode/getlist")
    public ResponseEntity<ApiResponseDTO<PageInfo<HaoSenKeHuData>>> getHsBackCodeList(HaoSenKeHuDataConditionDTO condition,
                                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsBackCodePageList(condition, pageNum, pageSize));
    }

    /**
     * 根据原始编码查询豪森返回编码单条数据
     */
    @GetMapping("/backhscode/getdetail")
    public ResponseEntity<ApiResponseDTO<HaoSenKeHuData>> getHsBackCodeDetail(@RequestParam("dataCode") String dataCode) {
        return ResponseEntity.ok(haoSenKeHuDataService.selectHsBackCodeByDataCode(dataCode));
    }

    // ==================== 总分院店编码导入 ====================

    /**
     * 导入总分院店编码 Excel 文件
     */
    @PostMapping("/branchcode/import")
    public ResponseEntity<ApiResponseDTO<Map<String, Integer>>> importBranchCode(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(haoSenKeHuDataService.importBranchCodeExcel(file));
    }
}
