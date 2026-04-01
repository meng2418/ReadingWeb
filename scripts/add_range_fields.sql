-- ================================================================
-- 添加 rangeStart 和 rangeEnd 字段到 note_info 表
-- 用于精确定位笔记在原文中的位置
-- ================================================================

-- 为 note_info 表添加 range_start 和 range_end 字段
-- 这两个字段必须有值，用于精确定位标注在章节中的位置
-- -1 表示未计算或不可用
ALTER TABLE `note_info` 
ADD COLUMN `range_start` INT NOT NULL DEFAULT 0 COMMENT '笔记在当前章节原文中的起始字符索引（包含空格、标点）' AFTER `content`,
ADD COLUMN `range_end` INT NOT NULL DEFAULT 0 COMMENT '笔记在当前章节原文中的结束字符索引（包含空格、标点）' AFTER `range_start`;

-- 为这两个新字段创建复合索引以提高查询性能
-- 用于快速查询特定章节内特定范围的笔记
CREATE INDEX `idx_chapter_range` ON `note_info` (`chapter_id`, `range_start`, `range_end`);

-- 验证修改
SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'note_info' 
  AND COLUMN_NAME IN ('range_start', 'range_end');

-- 数据验证和修复脚本（如果需要补充现有数据）
-- UPDATE `note_info` 
-- SET `range_start` = 0, `range_end` = LENGTH(`content`) - 1
-- WHERE `range_start` = 0 AND `range_end` = 0 AND `content` IS NOT NULL;

COMMIT;
