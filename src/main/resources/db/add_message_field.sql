-- 为 hr_batch 表添加 message 字段，用于存储处理状态信息
ALTER TABLE haosen_project..hr_batch ADD message NVARCHAR(1000) NULL;

-- 更新现有记录的消息字段（可选）
UPDATE haosen_project..hr_batch SET message = '处理成功' WHERE status = 1;
UPDATE haosen_project..hr_batch SET message = '处理失败' WHERE status = 2;
UPDATE haosen_project..hr_batch SET message = '正在处理' WHERE status = 0;
