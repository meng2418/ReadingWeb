package com.weread.dto.note;

import lombok.Data;

/**
 * 发布笔记响应DTO
 * 确保data字段是object类型
 */
@Data
public class NoteCreateResponseDTO {
    /**
     * 笔记信息
     */
    private NoteResponseDTO note;
}

