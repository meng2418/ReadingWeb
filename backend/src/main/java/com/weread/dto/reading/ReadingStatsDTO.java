package com.weread.dto.reading;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class ReadingStatsDTO {
    private Integer weeklyReadingTime;
    private Integer monthlyReadingTime;
    private Integer yearlyReadingTime;
    private Integer totalReadingTime;

    // getters and setters
    public Integer getWeeklyReadingTime() {
        return weeklyReadingTime;
    }

    public void setWeeklyReadingTime(Integer weeklyReadingTime) {
        this.weeklyReadingTime = weeklyReadingTime;
    }

    public Integer getMonthlyReadingTime() {
        return monthlyReadingTime;
    }

    public void setMonthlyReadingTime(Integer monthlyReadingTime) {
        this.monthlyReadingTime = monthlyReadingTime;
    }

    public Integer getYearlyReadingTime() {
        return yearlyReadingTime;
    }

    public void setYearlyReadingTime(Integer yearlyReadingTime) {
        this.yearlyReadingTime = yearlyReadingTime;
    }

    public Integer getTotalReadingTime() {
        return totalReadingTime;
    }

    public void setTotalReadingTime(Integer totalReadingTime) {
        this.totalReadingTime = totalReadingTime;
    }
}