-- 删除 hr_monitoring_data 表中的 product_id 列
-- 执行前请先备份数据！

-- 1. 查看表结构（执行前确认）
EXEC sp_help 'haosen_project..hr_monitoring_data';

-- 2. 删除 product_id 列
ALTER TABLE haosen_project..hr_monitoring_data
DROP COLUMN product_id;

-- 3. 如果临时表也需要删除
ALTER TABLE haosen_project..hr_monitoring_data_tmp
DROP COLUMN product_id;

-- 4. 验证删除结果
EXEC sp_help 'haosen_project..hr_monitoring_data';
EXEC sp_help 'haosen_project..hr_monitoring_data_tmp';
