-- 为 hr_org_relation 表添加 add_time 和 update_time 字段
ALTER TABLE haosen_project..hr_org_relation ADD add_time DATETIME NULL;
ALTER TABLE haosen_project..hr_org_relation ADD update_time DATETIME NULL;

-- 更新现有记录的时间字段（可选）
UPDATE haosen_project..hr_org_relation SET add_time = GETDATE(), update_time = GETDATE();
