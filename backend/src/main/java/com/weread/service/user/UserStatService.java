package com.weread.service.user;

import com.weread.vo.user.UserStatVO;

public interface UserStatService {

    UserStatVO getUserStats(Long userId);
    
    void incrementBookCount(Long userId, int count);
    
    void incrementNoteCount(Long userId, int count);
    
    void addReadingDuration(Long userId, int minutes);
}