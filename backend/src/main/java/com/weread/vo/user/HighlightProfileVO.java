package com.weread.vo.user;

import lombok.Data;

@Data
public class HighlightProfileVO {
    private Integer id;
    private String bookName;
    private String date;
    private String text;
    private String chapter;
}
