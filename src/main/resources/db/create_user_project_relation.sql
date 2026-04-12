-- 用户 - 项目关联表 (支持多对多关系)
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_project_relation]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[user_project_relation] (
        [id] INT IDENTITY(1,1) PRIMARY KEY,
        [user_id] NVARCHAR(64) NOT NULL,
        [project_id] NVARCHAR(64) NOT NULL,
        [has_permission] BIT NOT NULL DEFAULT 1, -- 1:有权限，0:无权限
        [create_time] DATETIME NOT NULL DEFAULT GETDATE(),
        [creator_id] NVARCHAR(64),
        CONSTRAINT [UK_user_project] UNIQUE ([user_id], [project_id])
    );

    -- 创建索引
    CREATE INDEX [IX_user_project_relation_user_id] ON [dbo].[user_project_relation] ([user_id]);
    CREATE INDEX [IX_user_project_relation_project_id] ON [dbo].[user_project_relation] ([project_id]);

    PRINT '创建表 user_project_relation 成功';
END
ELSE
BEGIN
    PRINT '表 user_project_relation 已存在';
END
GO
