-- 修复 user_privacy_settings 表：为 Hibernate 自动添加的无默认值列补上默认值
-- 执行：mysql -u root -p wechatreading_db < scripts/fix_user_privacy_settings.sql

USE wechatreading_db;

ALTER TABLE user_privacy_settings
  MODIFY COLUMN bookshelf bit(1) NULL DEFAULT b'1',
  MODIFY COLUMN reading_stats bit(1) NULL DEFAULT b'1',
  MODIFY COLUMN highlights bit(1) NULL DEFAULT b'1',
  MODIFY COLUMN thoughts bit(1) NULL DEFAULT b'1',
  MODIFY COLUMN book_reviews bit(1) NULL DEFAULT b'1',
  MODIFY COLUMN followers bit(1) NULL DEFAULT b'1',
  MODIFY COLUMN following bit(1) NULL DEFAULT b'1';
