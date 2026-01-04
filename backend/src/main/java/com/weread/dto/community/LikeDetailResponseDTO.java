package com.weread.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDetailResponseDTO {
    private Integer totalLikes;
    private List<LikedUserDTO> likedUsers;
}