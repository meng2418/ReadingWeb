package com.weread.vo.note;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 阅读笔记/高亮展示对象（Value Object）
 * 用于 API 接口返回给前端的笔记列表和详情数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteVO {

    private Integer noteId;

    // --- 笔记/高亮核心内容 ---
    private String content; // 笔记内容
    private String type;    // highligh, comment
    private String color;   // 标注颜色
    private Integer page;   // 页码
    
    // --- 关联信息 ---
    private Integer userId;    // 笔记作者ID
    private Integer bookId; // 书籍ID
    private Integer chapterId; // 章节ID
    
    // --- 状态/元数据 ---
    private Boolean isPublic; // 是否公开
    private LocalDateTime createdAt;
    
}