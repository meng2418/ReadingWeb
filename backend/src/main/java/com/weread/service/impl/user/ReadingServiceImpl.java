package com.weread.service.impl.user;

import com.weread.entity.user.UserReadingRecordEntity;
import com.weread.entity.user.ReadingMilestoneEntity;
import com.weread.entity.user.ReadingRewardEntity;
import com.weread.entity.book.BookEntity;
import com.weread.entity.asset.MemberCardEntity;
import com.weread.repository.user.UserReadingRecordRepository;
import com.weread.repository.user.ReadingRewardRepository;
import com.weread.repository.book.BookRepository;
import com.weread.repository.asset.MemberCardRepository;
import com.weread.repository.user.ReadingMilestoneRepository;
import com.weread.repository.note.NoteRepository;
import com.weread.service.user.ReadingService;
import com.weread.util.ImagePathUtils;
import com.weread.vo.user.MilestoneVO;
import com.weread.vo.user.ReadingTimeStatItemVO;
import com.weread.vo.user.TodayReadingTimeVO;
import com.weread.vo.user.TopBooksVO;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingServiceImpl implements ReadingService {

    private final UserReadingRecordRepository readingRecordRepository;
    private final ReadingRewardRepository readingRewardRepository;
    private final MemberCardRepository memberCardRepository;
    private final ReadingMilestoneRepository readingMilestoneRepository;
    private final BookRepository bookRepository;
    private final NoteRepository noteRepository;

    @Override
    @Transactional(readOnly = true)
    public TodayReadingTimeVO getTodayReadingTime(Integer userId) {
        LocalDate today = LocalDate.now();

        // 查询今日阅读时长
        Integer todayReadingTime = readingRecordRepository.sumReadingTimeByUserIdAndDate(userId, today);
        if (todayReadingTime == null) {
            todayReadingTime = 0;
        }

        // 构建返回对象
        TodayReadingTimeVO vo = new TodayReadingTimeVO();
        vo.setReadingTime(todayReadingTime);
        vo.setDailyGoal(30); // 默认每日目标30分钟

        // 计算完成率
        if (vo.getDailyGoal() > 0) {
            int rate = (int) ((todayReadingTime * 100.0) / vo.getDailyGoal());
            vo.setCompletionRate(Math.min(rate, 100));
        } else {
            vo.setCompletionRate(0);
        }

        // 检查是否有奖励可领取
        boolean hasClaimedToday = readingRewardRepository.existsByUserIdAndRewardDateAndRewardType(
                userId, today, "daily");
        vo.setHasRewardToClaim(!hasClaimedToday && todayReadingTime >= vo.getDailyGoal());

        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadingTimeStatItemVO> getReadingStats(Integer userId, String type, Integer year) {
        List<ReadingTimeStatItemVO> stats = new ArrayList<>();

        LocalDate now = LocalDate.now();
        if (year == null) {
            year = now.getYear();
        }

        switch (type.toLowerCase()) {
            case "daily":
                // 获取当月每日数据
                stats = getDailyStats(userId, year, now.getMonthValue());
                break;

            case "monthly":
                // 获取当年每月数据
                stats = getMonthlyStats(userId, year);
                break;

            case "yearly":
                // 获取最近5年数据
                stats = getYearlyStats(userId);
                break;

            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的统计类型");
        }

        return stats;
    }

    @Override
    @Transactional
    public boolean claimReadingReward(Integer userId, Integer minutes) {
        LocalDate today = LocalDate.now();
        
        // 如果未指定分钟数，默认30分钟
        if (minutes == null) {
            minutes = 30;
        }

        // 使用rewardType来区分不同的任务（格式：daily_5, daily_30, daily_60等）
        String rewardType = "daily_" + minutes;

        // 检查今日是否已领取该时长的任务
        boolean hasClaimed = readingRewardRepository.existsByUserIdAndRewardDateAndRewardType(
                userId, today, rewardType);

        if (hasClaimed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该任务今日已领取过");
        }

        // 检查今日阅读时长是否达标
        Integer todayReadingTime = readingRecordRepository.sumReadingTimeByUserIdAndDate(userId, today);
        if (todayReadingTime == null || todayReadingTime < minutes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                String.format("今日阅读时长未达标（需%d分钟）", minutes));
        }

        // 创建奖励记录
        ReadingRewardEntity reward = new ReadingRewardEntity();
        reward.setUserId(userId);
        reward.setRewardDate(today);
        reward.setRewardType(rewardType);
        reward.setRewardValue(2); // 2天体验卡
        reward.setDescription(String.format("每日阅读激励奖励（%d分钟）", minutes));
        reward.setIsClaimed(true);
        reward.setClaimedAt(LocalDateTime.now());
        readingRewardRepository.save(reward);

        // 创建会员卡（2天体验卡）
        MemberCardEntity card = new MemberCardEntity();
        card.setUserId(userId);
        card.setCardName("阅读激励体验卡");
        card.setDurationDays(2);
        card.setSourceType("reward");
        card.setSourceOrderNo("REWARD_" + System.currentTimeMillis());
        card.setStatus(MemberCardEntity.Status.VALID.getCode());
        card.setExpireAt(LocalDateTime.now().plusYears(1));
        memberCardRepository.save(card);

        return true;
    }

    @Override
    @Transactional
    public void addReadingRecord(Integer userId, Integer bookId, String bookTitle,
            Integer readingTime, Integer pageCount, Integer chapterId,
            String chapterTitle) {
        UserReadingRecordEntity record = new UserReadingRecordEntity();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBookTitle(bookTitle);
        record.setReadDate(LocalDate.now());
        record.setReadingTime(readingTime);
        record.setPageCount(pageCount);
        record.setChapterId(chapterId);
        record.setChapterTitle(chapterTitle);
        record.setRecordType(1); // 阅读记录

        readingRecordRepository.save(record);

        // 更新连续阅读天数（这里需要实现逻辑）
        updateConsecutiveReadingDays(userId);
    }

    // ============ 私有方法 ============

    private List<ReadingTimeStatItemVO> getDailyStats(Integer userId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 查询月度数据
        List<Object[]> dailyData = readingRecordRepository.findDailyStatsByUserIdAndDateRange(
                userId, startDate, endDate);

        List<ReadingTimeStatItemVO> stats = new ArrayList<>();

        // 填充整个月的日期
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

            ReadingTimeStatItemVO item = new ReadingTimeStatItemVO();
            item.setTime(dateStr);
            item.setReadingTime(0);

            // 查找是否有对应日期的数据
            for (Object[] data : dailyData) {
                LocalDate recordDate = (LocalDate) data[0];
                if (recordDate.equals(date)) {
                    item.setReadingTime(((Number) data[1]).intValue());
                    break;
                }
            }

            stats.add(item);
        }

        return stats;
    }

    private List<ReadingTimeStatItemVO> getMonthlyStats(Integer userId, Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Object[]> monthlyData = readingRecordRepository.findMonthlyStatsByUserIdAndDateRange(
                userId, startDate, endDate);

        List<ReadingTimeStatItemVO> stats = new ArrayList<>();

        // 填充全年12个月
        for (int month = 1; month <= 12; month++) {
            String monthStr = String.format("%04d-%02d", year, month);

            ReadingTimeStatItemVO item = new ReadingTimeStatItemVO();
            item.setTime(monthStr);
            item.setReadingTime(0);

            // 查找是否有对应月份的数据
            for (Object[] data : monthlyData) {
                String recordMonth = (String) data[0];
                if (recordMonth.equals(monthStr)) {
                    item.setReadingTime(((Number) data[1]).intValue());
                    break;
                }
            }

            stats.add(item);
        }

        return stats;
    }

    private List<ReadingTimeStatItemVO> getYearlyStats(Integer userId) {
        int currentYear = LocalDate.now().getYear();
        List<ReadingTimeStatItemVO> stats = new ArrayList<>();

        // 获取最近5年数据
        for (int year = currentYear - 4; year <= currentYear; year++) {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);

            Integer yearlyTotal = readingRecordRepository.sumReadingTimeByUserIdAndDateRange(
                    userId, startDate, endDate);

            ReadingTimeStatItemVO item = new ReadingTimeStatItemVO();
            item.setTime(String.valueOf(year));
            item.setReadingTime(yearlyTotal != null ? yearlyTotal : 0);
            stats.add(item);
        }

        return stats;
    }

    private void updateConsecutiveReadingDays(Integer userId) {
        // 这里实现更新连续阅读天数的逻辑
        // 可以查询最近几天的阅读记录，计算连续天数
        log.debug("更新用户 {} 的连续阅读天数", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public TopBooksVO getTopBooksByPeriod(Integer userId, String period) {
        TopBooksVO vo = new TopBooksVO();
        vo.setPeriod(period);

        LocalDate startDate = getStartDateByPeriod(period);
        LocalDate endDate = LocalDate.now();

        // 查询该时间段内阅读时间最长的书籍
        List<Object[]> topBooksData = readingRecordRepository.findTopBooksByUserIdAndPeriod(
                userId, startDate, endDate, org.springframework.data.domain.PageRequest.of(0, 3));

        List<TopBooksVO.TopBookItem> topBooksList = new ArrayList<>();

        for (Object[] data : topBooksData) {
            // data[0] = bookId, data[1] = bookTitle, data[2] = totalReadingTime
            Integer bookId = ((Number) data[0]).intValue();
            String bookTitle = (String) data[1];
            Integer readingTime = ((Number) data[2]).intValue();

            TopBooksVO.TopBookItem item = new TopBooksVO.TopBookItem();
            item.setBookId(bookId);
            item.setBookTitle(bookTitle);
            item.setReadingTime(readingTime);

            // 获取书籍封面和其他信息
            Optional<BookEntity> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent()) {
                BookEntity book = bookOpt.get();
                item.setCover(ImagePathUtils.processCoverPath(book.getCover()));
                item.setAuthorName(book.getAuthorName());
                item.setCategoryId(book.getCategoryId());
            }

            topBooksList.add(item);
        }

        // 转换为数组
        TopBooksVO.TopBookItem[] topBooks = topBooksList.toArray(new TopBooksVO.TopBookItem[0]);
        vo.setTopBooks(topBooks);
        return vo;
    }

    @Override
    @Transactional
    public void checkAndUpdateMilestones(Integer userId, String actionType, Object data) {
        // 根据actionType更新对应的里程碑
        switch (actionType) {
            case "read_book":
                updateTotalReadingMilestone(userId, data);
                break;
            case "add_note":
                updateNoteCountMilestone(userId, data);
                break;
            case "finish_book":
                updateFinishedReadingMilestone(userId, data);
                break;
        }
    }

    // ============ 私有方法 ============

    private LocalDate getStartDateByPeriod(String period) {
        LocalDate today = LocalDate.now();

        switch (period.toLowerCase()) {
            case "week":
                return today.minusDays(today.getDayOfWeek().getValue() - 1); // 本周一
            case "month":
                return today.withDayOfMonth(1); // 本月第一天
            case "year":
                return today.withDayOfYear(1); // 本年第一天
            case "total":
                return LocalDate.of(2000, 1, 1); // 很早的日期，表示全部
            default:
                return today.minusDays(today.getDayOfWeek().getValue() - 1); // 默认本周
        }
    }

    private void updateTotalReadingMilestone(Integer userId, Object data) {
        // 获取用户累计阅读的书籍数量
        Integer totalBooksRead = readingRecordRepository.countDistinctBooksByUserId(userId);

        // 里程碑阈值：20, 40, 60, 80, 100, 150, 200, 300, 500...
        Integer[] milestones = { 20, 40, 60, 80, 100, 150, 200, 300, 500 };

        for (int milestone : milestones) {
            if (totalBooksRead >= milestone) {
                // 检查是否已经记录过这个里程碑
                boolean exists = readingMilestoneRepository.existsByUserIdAndMilestoneTypeAndTargetCount(
                        userId, "total_reading", milestone);

                if (!exists) {
                    // 将之前的里程碑标记为非最新
                    readingMilestoneRepository.markAllAsNotLatest(userId, "total_reading");

                    // 创建新的里程碑记录
                    ReadingMilestoneEntity milestoneEntity = new ReadingMilestoneEntity();
                    milestoneEntity.setUserId(userId);
                    milestoneEntity.setMilestoneType("total_reading");
                    milestoneEntity.setTargetCount(milestone);
                    milestoneEntity.setMessage(String.format("恭喜！您已累计阅读%d本书", milestone));
                    milestoneEntity.setAchievedAt(LocalDateTime.now());
                    milestoneEntity.setIsLatest(true);

                    // 如果是书籍数据，设置书籍信息
                    if (data instanceof BookData) {
                        BookData bookData = (BookData) data;
                        milestoneEntity.setBookId(bookData.getBookId());
                        milestoneEntity.setBookTitle(bookData.getBookTitle());
                    }

                    readingMilestoneRepository.save(milestoneEntity);
                }
            }
        }
    }

    private void updateNoteCountMilestone(Integer userId, Object data) {
        // 获取用户笔记总数
        Integer totalNotesLong = noteRepository.countByUserId(userId);
        Integer totalNotes = totalNotesLong != null ? totalNotesLong.intValue() : 0;

        // 里程碑阈值：50, 100, 150, 200, 300, 500, 1000...
        Integer[] milestones = { 50, 100, 150, 200, 300, 500, 1000 };

        for (int milestone : milestones) {
            if (totalNotes >= milestone) {
                boolean exists = readingMilestoneRepository.existsByUserIdAndMilestoneTypeAndTargetCount(
                        userId, "note_count", milestone);

                if (!exists) {
                    readingMilestoneRepository.markAllAsNotLatest(userId, "note_count");

                    ReadingMilestoneEntity milestoneEntity = new ReadingMilestoneEntity();
                    milestoneEntity.setUserId(userId);
                    milestoneEntity.setMilestoneType("note_count");
                    milestoneEntity.setTargetCount(milestone);
                    milestoneEntity.setMessage(String.format("恭喜！您已创作%d条笔记", milestone));
                    milestoneEntity.setAchievedAt(LocalDateTime.now());
                    milestoneEntity.setIsLatest(true);

                    // 如果是笔记数据，设置笔记信息
                    if (data instanceof NoteData) {
                        NoteData noteData = (NoteData) data;
                        milestoneEntity.setNoteId(noteData.getNoteId());
                        milestoneEntity.setNoteContentPreview(noteData.getContentPreview());
                        milestoneEntity.setBookId(noteData.getBookId());
                        milestoneEntity.setBookTitle(noteData.getBookTitle());
                    }

                    readingMilestoneRepository.save(milestoneEntity);
                }
            }
        }
    }

    private void updateFinishedReadingMilestone(Integer userId, Object data) {
        // 获取用户读完的书籍数量
        Integer finishedBooks = readingRecordRepository.countFinishedBooksByUserId(userId);

        // 里程碑阈值：10, 20, 30, 50, 100, 200...
        Integer[] milestones = { 10, 20, 30, 50, 100, 200 };

        for (int milestone : milestones) {
            if (finishedBooks >= milestone) {
                boolean exists = readingMilestoneRepository.existsByUserIdAndMilestoneTypeAndTargetCount(
                        userId, "finished_reading", milestone);

                if (!exists) {
                    readingMilestoneRepository.markAllAsNotLatest(userId, "finished_reading");

                    ReadingMilestoneEntity milestoneEntity = new ReadingMilestoneEntity();
                    milestoneEntity.setUserId(userId);
                    milestoneEntity.setMilestoneType("finished_reading");
                    milestoneEntity.setTargetCount(milestone);
                    milestoneEntity.setMessage(String.format("恭喜！您已读完%d本书", milestone));
                    milestoneEntity.setAchievedAt(LocalDateTime.now());
                    milestoneEntity.setIsLatest(true);

                    // 如果是完成阅读数据，设置书籍信息
                    if (data instanceof BookData) {
                        BookData bookData = (BookData) data;
                        milestoneEntity.setBookId(bookData.getBookId());
                        milestoneEntity.setBookTitle(bookData.getBookTitle());
                    }

                    readingMilestoneRepository.save(milestoneEntity);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MilestoneVO getLatestMilestones(Integer userId) {
        MilestoneVO vo = new MilestoneVO();

        // 获取用户真实数据
        Integer totalBooksRead = readingRecordRepository.countDistinctBooksByUserId(userId);
        Integer totalBooksFinished = readingRecordRepository.countFinishedBooksByUserId(userId);
        Integer totalNotes = noteRepository.countByUserId(userId);

        // 1. 总阅读里程碑
        vo.setTotalReadingMilestone(calculateTotalReadingMilestone(userId, totalBooksRead));

        // 2. 笔记数量里程碑
        vo.setNoteCountMilestone(calculateNoteCountMilestone(userId, totalNotes));

        // 3. 读完本数里程碑
        vo.setFinishedReadingMilestone(calculateFinishedReadingMilestone(userId, totalBooksFinished));

        return vo;
    }

    private MilestoneVO.TotalReadingMilestone calculateTotalReadingMilestone(Integer userId, Integer totalBooksRead) {
        MilestoneVO.TotalReadingMilestone milestone = new MilestoneVO.TotalReadingMilestone();

        if (totalBooksRead == null || totalBooksRead == 0) {
            // 没有阅读记录，返回空对象但字段齐全
            milestone.setTargetCount(0);
            milestone.setAchievedBookTitle("");
            milestone.setAchievedAt(LocalDateTime.now());
            milestone.setMessage("开始您的第一本书吧！");
        } else {
            // 里程碑阈值：10, 20, 50, 100, 200, 500
            int[] thresholds = { 500, 200, 100, 50, 20, 10 };

            int achievedTarget = 0;
            for (int threshold : thresholds) {
                if (totalBooksRead >= threshold) {
                    achievedTarget = threshold;
                    break;
                }
            }

            // 如果连10本都没达到，使用5本作为初始里程碑
            if (achievedTarget == 0) {
                achievedTarget = 5;
            }

            // 获取最近阅读的书籍
            Pageable pageable = PageRequest.of(0, 1);
            List<UserReadingRecordEntity> recentRecords = readingRecordRepository
                    .findByUserIdOrderByReadDateDesc(userId, pageable);

            milestone.setTargetCount(achievedTarget);
            milestone.setAchievedBookTitle(
                    !recentRecords.isEmpty() ? recentRecords.get(0).getBookTitle() : "《您的书籍》");
            milestone.setAchievedAt(
                    !recentRecords.isEmpty() ? recentRecords.get(0).getReadDate().atStartOfDay() : LocalDateTime.now());
            milestone.setMessage(String.format("恭喜！您已累计阅读%d本书", achievedTarget));
        }

        return milestone;
    }

    private MilestoneVO.NoteCountMilestone calculateNoteCountMilestone(Integer userId, Integer totalNotes) {
        MilestoneVO.NoteCountMilestone milestone = new MilestoneVO.NoteCountMilestone();

        if (totalNotes == null || totalNotes == 0) {
            // 没有笔记记录
            milestone.setTargetCount(0);
            milestone.setAchievedBookTitle("");
            milestone.setAchievedNoteContent("");
            milestone.setAchievedAt(LocalDateTime.now());
            milestone.setMessage("写下您的第一条笔记吧！");
        } else {
            // 笔记里程碑：50, 100, 150, 200, 300, 500
            int[] thresholds = { 500, 300, 200, 150, 100, 50 };

            int achievedTarget = 0;
            for (int threshold : thresholds) {
                if (totalNotes >= threshold) {
                    achievedTarget = threshold;
                    break;
                }
            }

            // 如果连50条都没达到，使用10条作为初始里程碑
            if (achievedTarget == 0) {
                achievedTarget = 10;
            }

            // 获取最近的一条笔记
            List<Object[]> recentNoteData = noteRepository.findRecentNoteWithBook(userId, PageRequest.of(0, 1));

            milestone.setTargetCount(achievedTarget);

            if (!recentNoteData.isEmpty()) {
                Object[] data = recentNoteData.get(0);
                milestone.setAchievedBookTitle((String) data[0]); // 书名
                String noteContent = (String) data[1];
                milestone.setAchievedNoteContent(
                        noteContent.length() > 50 ? noteContent.substring(0, 50) + "..." : noteContent);
                milestone.setAchievedAt((LocalDateTime) data[2]); // 创建时间
            } else {
                milestone.setAchievedBookTitle("");
                milestone.setAchievedNoteContent("");
                milestone.setAchievedAt(LocalDateTime.now());
            }

            milestone.setMessage(String.format("恭喜！您已创作%d条笔记", achievedTarget));
        }

        return milestone;
    }

    private MilestoneVO.FinishedReadingMilestone calculateFinishedReadingMilestone(Integer userId,
            Integer totalBooksFinished) {
        MilestoneVO.FinishedReadingMilestone milestone = new MilestoneVO.FinishedReadingMilestone();

        if (totalBooksFinished == null || totalBooksFinished == 0) {
            // 没有读完的书籍
            milestone.setTargetCount(0);
            milestone.setAchievedBookTitle("");
            milestone.setAchievedAt(LocalDateTime.now());
            milestone.setMessage("读完您的第一本书吧！");
        } else {
            // 完成阅读里程碑：10, 20, 30, 50, 100
            int[] thresholds = { 100, 50, 30, 20, 10 };

            int achievedTarget = 0;
            for (int threshold : thresholds) {
                if (totalBooksFinished >= threshold) {
                    achievedTarget = threshold;
                    break;
                }
            }

            // 如果连10本都没达到，使用5本作为初始里程碑
            if (achievedTarget == 0) {
                achievedTarget = 5;
            }

            // 获取最近读完的书籍（假设有is_finished字段）
            Pageable pageable = PageRequest.of(0, 1);
            List<UserReadingRecordEntity> recentRecords = readingRecordRepository
                    .findByUserIdOrderByReadDateDesc(userId, pageable);

            milestone.setTargetCount(achievedTarget);
            milestone.setAchievedBookTitle(
                    !recentRecords.isEmpty() ? recentRecords.get(0).getBookTitle() : "《您的书籍》");
            milestone.setAchievedAt(
                    !recentRecords.isEmpty() ? recentRecords.get(0).getReadDate().atStartOfDay() : LocalDateTime.now());
            milestone.setMessage(String.format("恭喜！您已读完%d本书", achievedTarget));
        }

        return milestone;
    }

    // 辅助类
    @Data
    public static class BookData {
        private Integer bookId;
        private String bookTitle;
        private String author;
    }

    @Data
    public static class NoteData {
        private Long noteId;
        private String contentPreview;
        private Integer bookId;
        private String bookTitle;
    }

    @Override
    public Map<String, Object> getReadingStatsTimeline(Integer userId) {
        Map<String, Object> result = new HashMap<>();

        // week - 最近7天数据（按天）
        Map<String, Object> week = new HashMap<>();
        week.put("unit", "day");
        week.put("list", getWeekTimelineData(userId));
        result.put("week", week);

        // month - 最近30天数据（按天）
        Map<String, Object> month = new HashMap<>();
        month.put("unit", "day");
        month.put("list", getMonthTimelineData(userId));
        result.put("month", month);

        // year - 最近12个月数据（按月）
        Map<String, Object> year = new HashMap<>();
        year.put("unit", "month");
        year.put("list", getYearTimelineData(userId));
        result.put("year", year);

        // total - 所有数据（按年）
        Map<String, Object> total = new HashMap<>();
        total.put("unit", "year");
        total.put("list", getTotalTimelineData(userId));
        result.put("total", total);

        return result;
    }

    // 新增这几个方法
    private List<Map<String, Object>> getWeekTimelineData(Integer userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<Map<String, Object>> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 查询最近7天的阅读数据
        List<Object[]> dailyData = readingRecordRepository.findDailyStatsByUserIdAndDateRange(
                userId, startDate, endDate);

        // 转换为Map便于查找
        Map<LocalDate, Integer> dailyStats = new HashMap<>();
        for (Object[] data : dailyData) {
            LocalDate date = (LocalDate) data[0];
            Integer readingTime = ((Number) data[1]).intValue();
            dailyStats.put(date, readingTime);
        }

        // 构建7天数据
        for (int i = 6; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("readingTime", dailyStats.getOrDefault(date, 0));
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> getMonthTimelineData(Integer userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);

        List<Map<String, Object>> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Object[]> dailyData = readingRecordRepository.findDailyStatsByUserIdAndDateRange(
                userId, startDate, endDate);

        Map<LocalDate, Integer> dailyStats = new HashMap<>();
        for (Object[] data : dailyData) {
            LocalDate date = (LocalDate) data[0];
            Integer readingTime = ((Number) data[1]).intValue();
            dailyStats.put(date, readingTime);
        }

        // 构建30天数据
        for (int i = 29; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("readingTime", dailyStats.getOrDefault(date, 0));
            list.add(item);
        }

        return list;
    }

    private List<Map<String, Object>> getYearTimelineData(Integer userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(11).withDayOfMonth(1);

        List<Map<String, Object>> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        List<Object[]> monthlyData = readingRecordRepository.findMonthlyStatsByUserIdAndDateRange(
                userId, startDate, endDate);

        Map<String, Integer> monthlyStats = new HashMap<>();
        for (Object[] data : monthlyData) {
            String month = (String) data[0];
            Integer readingTime = ((Number) data[1]).intValue();
            monthlyStats.put(month, readingTime);
        }

        // 构建12个月数据
        for (int i = 11; i >= 0; i--) {
            YearMonth month = YearMonth.from(endDate.minusMonths(i));
            Map<String, Object> item = new HashMap<>();
            item.put("date", month.format(formatter));
            item.put("readingTime", monthlyStats.getOrDefault(month.format(formatter), 0));
            list.add(item);
        }

        return list;
    }

    private List<Map<String, Object>> getTotalTimelineData(Integer userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 获取所有年份的数据
        List<Object[]> yearlyData = readingRecordRepository.findYearlyStatsByUserId(userId);

        for (Object[] data : yearlyData) {
            Integer year = (Integer) data[0];
            Integer readingTime = ((Number) data[1]).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", year + "-01-01"); // 用年初日期表示年份
            item.put("readingTime", readingTime);
            list.add(item);
        }

        return list;
    }
}