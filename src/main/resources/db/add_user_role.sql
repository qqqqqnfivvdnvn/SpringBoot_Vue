-- 给用户表添加角色字段
IF NOT EXISTS (
    SELECT * FROM sys.columns
    WHERE object_id = OBJECT_ID(N'[dbo].[Users]')
    AND name = 'role'
)
BEGIN
    ALTER TABLE [dbo].[Users] ADD [role] NVARCHAR(20) NOT NULL DEFAULT 'user';
    PRINT 'Users 表添加 role 字段成功';
END
ELSE
BEGIN
    PRINT 'role 字段已存在';
END
GO

-- 添加索引提高查询效率
IF NOT EXISTS (
    SELECT * FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'[dbo].[Users]')
    AND name = 'IX_Users_role'
)
BEGIN
    CREATE INDEX [IX_Users_role] ON [dbo].[Users] ([role]);
    PRINT '创建 Users.role 索引成功';
END
GO
