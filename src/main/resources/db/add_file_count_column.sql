-- 为 hr_batch 表添加 file_count 字段（文件中实际的条数）
-- 执行时间：2026-04-16

USE haosen_project;
GO

-- 添加 file_count 字段（如果不存在）
IF NOT EXISTS (
    SELECT * FROM sys.columns
    WHERE object_id = OBJECT_ID('hr_batch')
    AND name = 'file_count'
)
BEGIN
    ALTER TABLE hr_batch ADD file_count INT DEFAULT 0;
    PRINT '成功添加 file_count 字段到 hr_batch 表';
END
ELSE
BEGIN
    PRINT 'hr_batch 表已存在 file_count 字段';
END
GO
