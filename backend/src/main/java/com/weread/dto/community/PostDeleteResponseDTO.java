package com.weread.dto.community;

import lombok.Data;

@Data
public class PostDeleteResponseDTO {
    private Integer deletedPostId;
    private Integer remainingPostCount;
}
