package com.weread.vo.user;

import lombok.Data;
@Data
public class ReadingStatsVO {

    private Integer weeklyReadingTime;
    private Integer monthlyReadingTime;
    private Integer yearlyReadingTime;
    private Integer totalReadingTime;

    private Integer weeklyReadCount;
    private Integer monthlyReadCount;
    private Integer yearlyReadCount;
    private Integer totalReadCount;

    private Integer weeklyFinishedCount;
    private Integer monthlyFinishedCount;
    private Integer yearlyFinishedCount;
    private Integer totalFinishedCount;

    private Integer weeklyNoteCount;
    private Integer monthlyNoteCount;
    private Integer yearlyNoteCount;
    private Integer totalNoteCount;
}
