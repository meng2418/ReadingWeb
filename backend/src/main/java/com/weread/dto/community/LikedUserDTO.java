package com.weread.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikedUserDTO {
    private Integer userId;
    private String username;
    private String avatar;
    private LocalDateTime likeTime;
}