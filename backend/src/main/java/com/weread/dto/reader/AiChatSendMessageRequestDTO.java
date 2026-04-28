package com.weread.dto.reader;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiChatSendMessageRequestDTO {
    @NotBlank(message = "message is required")
    private String message;
}
