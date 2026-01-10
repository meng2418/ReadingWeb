package com.weread.dto.note;

import lombok.Data;
import java.util.List;

/**
 * 用户笔记瀑布流响应DTO
 * 对应接口：GET /user/notes
 */
@Data
public class UserNotesResponseDTO {
    private List<UserNoteDTO> notes; // 笔记列表
    private Boolean hasMore; // 是否还有更多数据
    private String nextCursor; // 下一个游标
    private Integer noteCount; // 用户总笔记数
}

