-- 为模糊匹配批次表和汇总表添加 data_type 字段
-- 用于区分医院数据和药店数据

-- 1. 为批次表添加 data_type 字段
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('haosen_project..md_fuzzy_match_batch') AND name = 'data_type')
BEGIN
    ALTER TABLE haosen_project..md_fuzzy_match_batch ADD data_type NVARCHAR(20) NULL;
    PRINT '批次表添加 data_type 字段成功';
END
ELSE
BEGIN
    PRINT '批次表已存在 data_type 字段';
END

-- 2. 为汇总表添加 data_type 字段
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('haosen_project..md_fuzzy_match_summary') AND name = 'data_type')
BEGIN
    ALTER TABLE haosen_project..md_fuzzy_match_summary ADD data_type NVARCHAR(20) NULL;
    PRINT '汇总表添加 data_type 字段成功';
END
ELSE
BEGIN
    PRINT '汇总表已存在 data_type 字段';
END

-- 3. 为批次表的 data_type 字段添加索引
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID('haosen_project..md_fuzzy_match_batch') AND name = 'IX_md_fuzzy_match_batch_data_type')
BEGIN
    CREATE INDEX IX_md_fuzzy_match_batch_data_type ON haosen_project..md_fuzzy_match_batch(data_type);
    PRINT '批次表 data_type 索引创建成功';
END

-- 4. 为汇总表的 data_type 字段添加索引
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID('haosen_project..md_fuzzy_match_summary') AND name = 'IX_md_fuzzy_match_summary_data_type')
BEGIN
    CREATE INDEX IX_md_fuzzy_match_summary_data_type ON haosen_project..md_fuzzy_match_summary(data_type);
    PRINT '汇总表 data_type 索引创建成功';
END

GO
PRINT '数据库迁移完成！';
GO
