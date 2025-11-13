package com.weread.dto.bookshelf;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 书架批量操作的参数DTO（对应POST /bookshelf/batch的请求体）
 */
@Data
public class BookshelfBatchDTO {
    @NotNull(message = "操作类型不能为空")
    private String action; // 可选值：delete/update-status

    @NotEmpty(message = "书籍ID列表不能为空")
    private List<Integer> bookIds;

    private String status; // 当action为update-status时必填，可选值：unread/reading/finished
}