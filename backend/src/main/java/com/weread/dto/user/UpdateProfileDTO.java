package com.weread.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    @Size(min = 2, max = 20, message = "昵称长度需在2-20位之间")
    private String username;
    private String avatar;
    private String bio;
}