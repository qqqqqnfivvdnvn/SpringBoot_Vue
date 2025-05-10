/*
 Navicat Premium Dump SQL

 Source Server         : localhost_sqlserver
 Source Server Type    : SQL Server
 Source Server Version : 15002000 (15.00.2000)
 Source Catalog        : test
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 15002000 (15.00.2000)
 File Encoding         : 65001

 Date: 11/05/2025 01:00:11
*/


-- ----------------------------
-- Table structure for projects
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[projects]') AND type IN ('U'))
	DROP TABLE [dbo].[projects]
GO

CREATE TABLE [dbo].[projects] (
  [id] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [description] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [icon] nvarchar(20) COLLATE Chinese_PRC_CI_AS  NULL,
  [color] nvarchar(20) COLLATE Chinese_PRC_CI_AS  NULL,
  [route_name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NULL,
  [addtime] datetime2(7)  NULL,
  [user_id] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NULL,
  [user_name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[projects] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'id',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'项目名称',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'项目介绍',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'description'
GO

EXEC sp_addextendedproperty
'MS_Description', N'图标名称',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'icon'
GO

EXEC sp_addextendedproperty
'MS_Description', N'颜色',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'color'
GO

EXEC sp_addextendedproperty
'MS_Description', N'路由名称',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'route_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'添加时间',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'addtime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户id',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'user_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户',
'SCHEMA', N'dbo',
'TABLE', N'projects',
'COLUMN', N'user_name'
GO


-- ----------------------------
-- Records of projects
-- ----------------------------
INSERT INTO [dbo].[projects] ([id], [name], [description], [icon], [color], [route_name], [addtime], [user_id], [user_name]) VALUES (N'43b10682bcb74cd4b5444f524e5fef89', N'豪森主数据管理系统', N'查看仪表盘、数据功能、数据申诉功能', N'users', N'#90be6d', N'HaosenHome', N'2025-05-11 00:50:13.9220000', N'1c9ab9ee0fcf4c5ba13a3c28b6d47de0', N'admin')
GO


-- ----------------------------
-- Primary Key structure for table projects
-- ----------------------------
ALTER TABLE [dbo].[projects] ADD CONSTRAINT [PK__projects__3213E83F771F42B3] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

