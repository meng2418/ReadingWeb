package com.weread.service.impl.user;

import com.weread.entity.user.UserReadingRecordEntity;
import com.weread.entity.user.ReadingMilestoneEntity;
import com.weread.entity.user.ReadingRewardEntity;
import com.weread.entity.asset.MemberCardEntity;
import com.weread.entity.book.BookEntity;
import com.weread.repository.user.UserReadingRecordRepository;
import com.weread.repository.user.ReadingRewardRepository;
import com.weread.repository.asset.MemberCardRepository;
import com.weread.repository.book.BookRepository;
import com.weread.repository.user.ReadingMilestoneRepository;
import com.weread.repository.note.NoteRepository;
import com.weread.service.user.ReadingService;
import com.weread.vo.user.MilestoneVO;
import com.weread.vo.user.ReadingTimeStatItemVO;
import com.weread.vo.user.TodayReadingTimeVO;
import com.weread.vo.user.TopBooksVO;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    public boolean claimReadingReward(Integer userId) {
        LocalDate today = LocalDate.now();

        // 检查今日是否已领取
        boolean hasClaimed = readingRewardRepository.existsByUserIdAndRewardDateAndRewardType(
                userId, today, "daily");

        if (hasClaimed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "今日已领取过奖励");
        }

        // 检查今日阅读时长是否达标（30分钟）
        Integer todayReadingTime = readingRecordRepository.sumReadingTimeByUserIdAndDate(userId, today);
        if (todayReadingTime == null || todayReadingTime < 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "今日阅读时长未达标（需30分钟）");
        }

        // 创建奖励记录
        ReadingRewardEntity reward = new ReadingRewardEntity();
        reward.setUserId(userId);
        reward.setRewardDate(today);
        reward.setRewardType("daily");
        reward.setRewardValue(1); // 1天体验卡
        reward.setDescription("每日阅读激励奖励");
        reward.setIsClaimed(true);
        reward.setClaimedAt(LocalDateTime.now());
        readingRewardRepository.save(reward);

        // 创建会员卡（1天体验卡）
        MemberCardEntity card = new MemberCardEntity();
        card.setUserId(userId);
        card.setCardName("阅读激励体验卡");
        card.setDurationDays(1);
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

        TopBooksVO.TopBookItem[] topBooks = new TopBooksVO.TopBookItem[Math.min(topBooksData.size(), 3)];

        for (int i = 0; i < Math.min(topBooksData.size(), 3); i++) {
            Object[] data = topBooksData.get(i);
            TopBooksVO.TopBookItem item = new TopBooksVO.TopBookItem();
            item.setBookId((Integer) data[0]);
            item.setBookTitle((String) data[1]);
            item.setReadingTime(((Number) data[2]).intValue());

            // 获取书籍封面和其他信息
            Optional<BookEntity> bookOpt = bookRepository.findById(item.getBookId());
            if (bookOpt.isPresent()) {
                BookEntity book = bookOpt.get();
                item.setCover(book.getCover());
                item.setAuthorName(book.getAuthorName());
                item.setCategoryId(book.getCategoryId());
            }

            topBooks[i] = item;
        }

        vo.setTopBooks(topBooks);
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public MilestoneVO getLatestMilestones(Integer userId) {
        MilestoneVO vo = new MilestoneVO();

        // 获取累计阅读里程碑
        Optional<ReadingMilestoneEntity> totalReadingOpt = readingMilestoneRepository
                .findTopByUserIdAndMilestoneTypeAndIsLatestTrueOrderByTargetCountDesc(
                        userId, "total_reading");
        totalReadingOpt.ifPresent(milestone -> {
            MilestoneVO.TotalReadingMilestone m = new MilestoneVO.TotalReadingMilestone();
            m.setTargetCount(milestone.getTargetCount());
            m.setAchievedBookTitle(milestone.getBookTitle());
            m.setAchievedAt(milestone.getAchievedAt());
            m.setMessage(milestone.getMessage());
            vo.setTotalReadingMilestone(m);
        });

        // 获取笔记数量里程碑
        Optional<ReadingMilestoneEntity> noteCountOpt = readingMilestoneRepository
                .findTopByUserIdAndMilestoneTypeAndIsLatestTrueOrderByTargetCountDesc(
                        userId, "note_count");
        noteCountOpt.ifPresent(milestone -> {
            MilestoneVO.NoteCountMilestone m = new MilestoneVO.NoteCountMilestone();
            m.setTargetCount(milestone.getTargetCount());
            m.setAchievedBookTitle(milestone.getBookTitle());
            m.setAchievedNoteContent(milestone.getNoteContentPreview());
            m.setAchievedAt(milestone.getAchievedAt());
            m.setMessage(milestone.getMessage());
            vo.setNoteCountMilestone(m);
        });

        // 获取读完本数里程碑
        Optional<ReadingMilestoneEntity> finishedReadingOpt = readingMilestoneRepository
                .findTopByUserIdAndMilestoneTypeAndIsLatestTrueOrderByTargetCountDesc(
                        userId, "finished_reading");
        finishedReadingOpt.ifPresent(milestone -> {
            MilestoneVO.FinishedReadingMilestone m = new MilestoneVO.FinishedReadingMilestone();
            m.setTargetCount(milestone.getTargetCount());
            m.setAchievedBookTitle(milestone.getBookTitle());
            m.setAchievedAt(milestone.getAchievedAt());
            m.setMessage(milestone.getMessage());
            vo.setFinishedReadingMilestone(m);
        });

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
        Integer totalNotes = noteRepository.countByUserId(userId);

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
}