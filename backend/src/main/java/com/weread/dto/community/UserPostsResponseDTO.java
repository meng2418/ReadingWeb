package com.weread.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPostsResponseDTO {
    private List<PostSquareDTO> posts;  // 使用已有的PostSquareDTO
    private Boolean hasMore;
    private String nextCursor;
    private Integer postCount;
    private Integer commentCount;
    private Integer likeCount;
}