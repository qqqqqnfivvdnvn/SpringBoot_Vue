-- 迁移脚本：更新 user_project_relation 表结构
-- 1. 删除 permission_type 字段
-- 2. 添加 has_permission 字段

-- 删除 permission_type 字段
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_project_relation]') AND name = 'permission_type')
BEGIN
    ALTER TABLE [dbo].[user_project_relation] DROP COLUMN [permission_type];
    PRINT '成功删除 permission_type 字段';
END
ELSE
BEGIN
    PRINT 'permission_type 字段不存在，无需删除';
END
GO

-- 添加 has_permission 字段（如果不存在）
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_project_relation]') AND name = 'has_permission')
BEGIN
    ALTER TABLE [dbo].[user_project_relation] ADD [has_permission] BIT NOT NULL DEFAULT 1;
    PRINT '成功添加 has_permission 字段 (默认值：1)';
END
ELSE
BEGIN
    PRINT 'has_permission 字段已存在';
END
GO

-- 更新现有记录的 has_permission 为 1（确保旧数据有权限）
UPDATE [dbo].[user_project_relation] SET [has_permission] = 1 WHERE [has_permission] IS NULL;
PRINT '已更新现有记录的 has_permission 为 1';
GO
