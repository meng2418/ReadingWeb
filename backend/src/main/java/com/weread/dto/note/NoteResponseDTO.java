package com.weread.dto.note;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 笔记响应DTO
 */
@Data
public class NoteResponseDTO {
    private Integer noteId;
    private String quote;
    private String lineType;
    private String noteContent; // 对应thought
    private LocalDateTime createdAt;
}

