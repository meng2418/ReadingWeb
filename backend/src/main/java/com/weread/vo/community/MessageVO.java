package com.weread.vo.community;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息通知展示对象（Value Object）
 * 用于 API 接口返回给前端的消息列表数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {

    private Long messageId;

    /**
     * 消息类型: system, like, comment, follow
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 相关实体ID (如 PostId, CommentId 等)
     */
    private Long relatedId;
    
    /**
     * 消息创建时间
     */
    private LocalDateTime createdAt;
    
    // 注意：VO 中不包含 userId，因为前端通常只查询自己的消息
}