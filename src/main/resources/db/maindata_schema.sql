-- 主数据项目数据库表结构
-- 数据库：haosen_project (SQL Server)

-- ========================================
-- 1. 批次管理表 (md_location_batch)
-- ========================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[md_location_batch]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[md_location_batch] (
        [batch_id] NVARCHAR(64) NOT NULL PRIMARY KEY,
        [create_time] DATETIME NOT NULL DEFAULT GETDATE(),
        [update_time] DATETIME NOT NULL DEFAULT GETDATE(),
        [status] INT NOT NULL DEFAULT 0,  -- 0:处理中 1:已处理 2:处理失败
        [original_filename] NVARCHAR(255) NOT NULL,
        [message] NVARCHAR(500)
    );

    -- 创建索引
    CREATE INDEX [IX_md_location_batch_create_time] ON [dbo].[md_location_batch] ([create_time] DESC);
    CREATE INDEX [IX_md_location_batch_status] ON [dbo].[md_location_batch] ([status]);

    PRINT '创建表 md_location_batch 成功';
END
ELSE
BEGIN
    PRINT '表 md_location_batch 已存在';
END
GO

-- ========================================
-- 2. 地理位置数据表 (md_location)
-- ========================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[md_location]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[md_location] (
        [id] INT IDENTITY(1,1) PRIMARY KEY,
        [batch_id] NVARCHAR(64) NOT NULL,
        [original_id] NVARCHAR(64),          -- 原始 ID
        [original_name] NVARCHAR(255),       -- 原始名称
        [original_province] NVARCHAR(100),   -- 原始省份
        [original_address] NVARCHAR(500),    -- 原始地址
        [area_name] NVARCHAR(100),           -- 区县名称
        [area_id] NVARCHAR(50),              -- 区县编码
        [lng_lat] NVARCHAR(100),             -- 经纬度 (经度，纬度)
        [province] NVARCHAR(100),            -- 省份
        [city] NVARCHAR(100),                -- 城市
        [create_time] DATETIME NOT NULL DEFAULT GETDATE(),
        [update_time] DATETIME NOT NULL DEFAULT GETDATE()
    );

    -- 创建索引
    CREATE INDEX [IX_md_location_batch_id] ON [dbo].[md_location] ([batch_id]);
    CREATE INDEX [IX_md_location_original_name] ON [dbo].[md_location] ([original_name]);
    CREATE INDEX [IX_md_location_area_id] ON [dbo].[md_location] ([area_id]);

    PRINT '创建表 md_location 成功';
END
ELSE
BEGIN
    PRINT '表 md_location 已存在';
END
GO
