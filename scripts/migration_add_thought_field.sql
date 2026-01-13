-- ============================================
-- 数据库迁移脚本：为 note_info 表添加 thought 字段
-- 用于存储想法（批注）内容
-- ============================================

USE wechatreading_db;

-- 1. 添加 thought 字段（想法内容）
-- 使用 TEXT 类型以支持较长的想法内容
ALTER TABLE note_info 
ADD COLUMN thought TEXT NULL COMMENT '想法/批注内容，仅当 type = "thought" 时使用';

-- 2. 添加索引以提高查询性能（可选，根据实际查询需求决定）
-- 如果经常需要查询想法类型的笔记，可以添加以下索引
-- CREATE INDEX idx_note_type ON note_info(type);
-- CREATE INDEX idx_note_type_created ON note_info(type, created_at DESC);

-- 3. 验证字段添加成功
-- SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = 'wechatreading_db' 
--   AND TABLE_NAME = 'note_info' 
--   AND COLUMN_NAME = 'thought';
