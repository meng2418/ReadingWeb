package com.weread.vo.user;

import lombok.Data;

@Data
public class ThoughtProfileVO {
    private Integer id;
    private String bookName;
    private String date;
    private String thought;
    private String quote;
}
