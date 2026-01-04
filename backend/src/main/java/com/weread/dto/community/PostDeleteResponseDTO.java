package com.weread.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDeleteResponseDTO {
    private Boolean success;
    private String message;
    private Integer deletedPostId;
    private Integer remainingPostCount;
    private Object data; // 扩展字段
}
