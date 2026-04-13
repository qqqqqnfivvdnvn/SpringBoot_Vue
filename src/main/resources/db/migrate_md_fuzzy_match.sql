-- 主数据模糊匹配功能数据库迁移脚本 (SQL Server 版本)
-- 创建批次表和汇总数据表

-- 创建模糊匹配批次表
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='md_fuzzy_match_batch' AND xtype='U')
BEGIN
    CREATE TABLE md_fuzzy_match_batch (
        batch_id VARCHAR(64) PRIMARY KEY,
        status INT DEFAULT 0,
        original_filename VARCHAR(255),
        message TEXT,
        create_time DATETIME DEFAULT GETDATE(),
        update_time DATETIME DEFAULT GETDATE()
    )

    CREATE INDEX idx_status ON md_fuzzy_match_batch(status)
    CREATE INDEX idx_create_time ON md_fuzzy_match_batch(create_time)
END

-- 创建模糊匹配汇总数据表
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='md_fuzzy_match_summary' AND xtype='U')
BEGIN
    CREATE TABLE md_fuzzy_match_summary (
        batch_id VARCHAR(64) NOT NULL,
        id VARCHAR(64),
        original_province VARCHAR(64),
        original_name VARCHAR(255),
        original_address VARCHAR(500),
        keyid VARCHAR(64),
        name VARCHAR(255),
        remark VARCHAR(500),
        create_time DATETIME DEFAULT GETDATE(),
        update_time DATETIME DEFAULT GETDATE()
    )

    CREATE INDEX idx_batch_id ON md_fuzzy_match_summary(batch_id)
    CREATE INDEX idx_keyid ON md_fuzzy_match_summary(keyid)
END
