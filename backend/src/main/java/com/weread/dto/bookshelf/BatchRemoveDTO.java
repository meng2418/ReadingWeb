package com.weread.dto.bookshelf;

import lombok.Data;
import java.util.List;

@Data
public class BatchRemoveDTO {
    // 接口要求的bookIds列表
    private List<Integer> bookIds;
}