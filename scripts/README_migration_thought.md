# 数据库迁移说明：添加 thought 字段

## 概述
本次迁移为 `note_info` 表添加 `thought` 字段，用于存储想法（批注）内容。

## 变更内容

### 1. 数据库变更
- **表名**: `note_info`
- **新增字段**: `thought` (TEXT, NULL)
- **用途**: 存储用户的想法/批注内容，仅当 `type = "thought"` 时使用

### 2. 代码变更
- ✅ `NoteEntity.java`: 添加 `thought` 字段
- ✅ `NoteServiceImpl.java`: 更新所有相关方法以使用新字段

## 执行步骤

### 步骤 1: 备份数据库（重要！）
```bash
# 备份整个数据库
mysqldump -u root -p wechatreading_db > backup_before_thought_migration.sql

# 或者只备份 note_info 表
mysqldump -u root -p wechatreading_db note_info > backup_note_info.sql
```

### 步骤 2: 执行迁移脚本
```bash
# 方式1: 使用 MySQL 命令行
mysql -u root -p wechatreading_db < scripts/migration_add_thought_field.sql

# 方式2: 在 MySQL 客户端中执行
mysql -u root -p
USE wechatreading_db;
SOURCE scripts/migration_add_thought_field.sql;
```

### 步骤 3: 验证迁移结果
```sql
-- 检查字段是否添加成功
DESCRIBE note_info;

-- 或者
SHOW COLUMNS FROM note_info LIKE 'thought';

-- 检查现有数据（应该都是 NULL）
SELECT note_id, type, thought, content 
FROM note_info 
LIMIT 10;
```

### 步骤 4: 重启应用
```bash
# 重启 Spring Boot 应用，使代码变更生效
# 确保新的 NoteEntity 字段被正确映射
```

## 回滚方案（如果需要）

如果迁移出现问题，可以执行以下 SQL 回滚：

```sql
USE wechatreading_db;

-- 删除 thought 字段
ALTER TABLE note_info DROP COLUMN thought;
```

然后恢复代码到之前的版本。

## 数据迁移注意事项

1. **现有数据**: 迁移后，所有现有笔记的 `thought` 字段将为 `NULL`，这是正常的
2. **新数据**: 新创建的想法（`type = "thought"`）将正确存储 `thought` 内容
3. **兼容性**: 
   - 划线（`type = "highlight"`）的 `thought` 字段为 `NULL`
   - 想法（`type = "thought"`）的 `thought` 字段包含实际内容

## 验证清单

- [ ] 数据库迁移脚本执行成功
- [ ] `note_info` 表包含 `thought` 字段
- [ ] 应用启动无错误
- [ ] 创建想法功能正常工作
- [ ] 查询想法功能正常返回 `thought` 内容
- [ ] 划线功能不受影响（`thought` 为 `NULL`）

## 后续优化建议

1. **索引优化**: 如果经常需要查询想法类型的笔记，可以考虑添加索引：
   ```sql
   CREATE INDEX idx_note_type ON note_info(type);
   CREATE INDEX idx_note_type_created ON note_info(type, created_at DESC);
   ```

2. **数据清理**: 如果未来需要，可以清理无效数据：
   ```sql
   -- 删除 type = "thought" 但 thought 为 NULL 的记录（如果有）
   DELETE FROM note_info 
   WHERE type = 'thought' AND thought IS NULL;
   ```
