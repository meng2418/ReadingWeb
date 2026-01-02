package com.weread.vo.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MilestoneVO {
    
    private TotalReadingMilestone totalReadingMilestone;
    private NoteCountMilestone noteCountMilestone;
    private FinishedReadingMilestone finishedReadingMilestone;
    
    @Data
    public static class TotalReadingMilestone {
        private Integer targetCount;
        private String achievedBookTitle;
        private LocalDateTime achievedAt;
        private String message;
    }
    
    @Data
    public static class NoteCountMilestone {
        private Integer targetCount;
        private String achievedBookTitle;
        private String achievedNoteContent;
        private LocalDateTime achievedAt;
        private String message;
    }
    
    @Data
    public static class FinishedReadingMilestone {
        private Integer targetCount;
        private String achievedBookTitle;
        private LocalDateTime achievedAt;
        private String message;
    }
}