# MdLocation province 字段保留

## 变更说明
根据用户需求，`md_location` 表 **保留** `province` 字段。

## 保留的字段
- `province` - 省份字段（高德地图 API 解析后的省份信息）
- `originalProvince` - 原始省份（导入时的数据）
- `city` - 城市字段
- `areaName`, `areaId` - 区县信息
- `lngLat` - 经纬度

## 修改的文件

### 后端文件
1. **MdLocation.java** - 保留 `province` 字段
2. **MdLocationMapper.xml** - 保留：
   - `MdLocationExportVOMap` ResultMap 中的 province 映射
   - `MdLocationMap` ResultMap 中的 province 映射
   - `selectByCondition` 查询中的 province 字段和 WHERE 条件
   - `batchInsert` 语句中的 province 列和值
   - `selectExportData` 查询中的 province WHERE 条件
3. **MdLocationExportVO.java** - 保留 `province` 属性
4. **MdLocationVO.java** - 保留 `province` 属性
5. **MdLocationConditionDTO.java** - 保留 `province` 查询条件
6. **MdLocationBatchAsyncService.java** - 保留设置 province 值的代码

### 前端文件
1. **MdLocationView.vue** - 保留：
   - 表格中的省份列显示
   - 搜索表单中的省份筛选输入框
   - `searchForm` 中的 `province` 属性
   - `resetSearch` 中的 `province` 重置代码
