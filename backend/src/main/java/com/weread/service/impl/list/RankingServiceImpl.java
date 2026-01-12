package com.weread.service.impl.list;

import com.weread.dto.book.SimpleBookDTO;
import com.weread.entity.book.BookEntity;
import com.weread.entity.author.AuthorEntity;
import com.weread.repository.book.BookRepository;
import com.weread.service.list.RankingService;
import com.weread.util.ImagePathUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingServiceImpl implements RankingService {

    private final BookRepository bookRepository;

    private static final int RANKING_SIZE = 10;
    private static final int NEW_BOOK_MONTHS = 3;

    // 缓存近期阅读数据
    private Map<Integer, RecentReadStats> weeklyReadStats = new HashMap<>();
    private Map<Integer, RecentReadStats> monthlyReadStats = new HashMap<>();

    @Override
    public List<SimpleBookDTO> getRankingBooks(String type) {
        log.info("获取{}榜单", type);

        switch (type.toLowerCase()) {
            case "weekly":
                return getWeeklyRanking();
            case "monthly":
                return getMonthlyRanking();
            case "new":
                return getNewBooksRanking();
            case "masterpiece":
                return getMasterpieceRanking();
            default:
                log.warn("未知榜单类型: {}, 使用周榜替代", type);
                return getWeeklyRanking();
        }
    }

    @Override
    public List<SimpleBookDTO> getWeeklyRanking() {
        try {
            // 加载周阅读数据
            loadRecentReadStats(7);

            // 获取所有已发布书籍
            List<BookEntity> allBooks = bookRepository.findAllPublishedBooks();

            if (allBooks.isEmpty()) {
                log.warn("没有已发布的书籍");
                return getFallbackRanking();
            }

            // 计算周榜得分
            List<BookScore> scoredBooks = allBooks.stream()
                    .map(book -> {
                        double score = calculateWeeklyScore(book);
                        return new BookScore(book, score);
                    })
                    .sorted(Comparator.comparingDouble(BookScore::getScore).reversed())
                    .limit(RANKING_SIZE)
                    .collect(Collectors.toList());

            // 转换为DTO
            return convertToDTOs(
                    scoredBooks.stream()
                            .map(BookScore::getBook)
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("获取周榜失败", e);
            return getFallbackRanking();
        }
    }

    /**
     * 周榜评分算法
     * 权重：近期热度(60%) + 质量评分(30%) + 新鲜度(10%)
     */
    private double calculateWeeklyScore(BookEntity book) {
        // 基础数据（安全获取，避免空指针）
        Float rating = Optional.ofNullable(book.getRating()).orElse(0.0f);
        Integer readCount = Optional.ofNullable(book.getReadCount()).orElse(0);
        LocalDateTime createdAt = book.getCreatedAt();

        double ratingValue = rating.doubleValue();
        int readCountValue = readCount;

        // 1. 近期热度（基于最近7天阅读记录）
        double recentHeat = calculateRecentHeat(book.getBookId(), 7);

        // 2. 质量评分（归一化到0-1）
        double normalizedRating = normalize(ratingValue, 0.0, 5.0);

        // 3. 阅读人数（对数归一化）
        double normalizedReadCount = normalizeLog(readCountValue);

        // 4. 新鲜度因子（30天内为新书）
        double freshnessFactor = calculateFreshnessFactor(createdAt, 30);

        // 周榜权重公式
        double score = recentHeat * 0.6 + // 近期热度 60%
                normalizedRating * 0.3 + // 质量评分 30%
                freshnessFactor * 0.1; // 新鲜度 10%

        // 阅读人数基础加分
        score += normalizedReadCount * 0.05;

        // 保留两位小数
        return Math.round(score * 100.0) / 100.0;
    }

    @Override
    public List<SimpleBookDTO> getMonthlyRanking() {
        try {
            // 加载月阅读数据
            loadRecentReadStats(30);

            List<BookEntity> allBooks = bookRepository.findAllPublishedBooks();

            if (allBooks.isEmpty()) {
                return getFallbackRanking();
            }

            // 计算月榜得分
            List<BookScore> scoredBooks = allBooks.stream()
                    .map(book -> {
                        double score = calculateMonthlyScore(book);
                        return new BookScore(book, score);
                    })
                    .sorted(Comparator.comparingDouble(BookScore::getScore).reversed())
                    .limit(RANKING_SIZE)
                    .collect(Collectors.toList());

            return convertToDTOs(
                    scoredBooks.stream()
                            .map(BookScore::getBook)
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("获取月榜失败", e);
            return getFallbackRanking();
        }
    }

    /**
     * 月榜评分算法
     * 权重：长期热度(50%) + 质量评分(40%) + 稳定性(10%)
     */
    private double calculateMonthlyScore(BookEntity book) {
        Float rating = Optional.ofNullable(book.getRating()).orElse(0.0f);
        Integer readCount = Optional.ofNullable(book.getReadCount()).orElse(0);

        double ratingValue = rating.doubleValue();
        int readCountValue = readCount;

        // 1. 长期热度（最近30天）
        double longTermHeat = calculateRecentHeat(book.getBookId(), 30);

        // 2. 质量评分
        double normalizedRating = normalize(ratingValue, 0.0, 5.0);

        // 3. 阅读人数稳定性
        double normalizedReadCount = normalizeLog(readCountValue);

        // 4. 评分稳定性
        double ratingStability = calculateRatingStability(book);

        // 月榜权重公式
        double score = longTermHeat * 0.5 + // 长期热度 50%
                normalizedRating * 0.4 + // 质量评分 40%
                ratingStability * 0.1; // 稳定性 10%

        // 总阅读人数基础加分
        score += normalizedReadCount * 0.05;

        return Math.round(score * 100.0) / 100.0;
    }

    @Override
    public List<SimpleBookDTO> getNewBooksRanking() {
        try {
            // 核心修改：LocalDate转LocalDateTime（补0点）
            LocalDateTime threeMonthsAgo = LocalDate.now()
                    .minusMonths(NEW_BOOK_MONTHS)
                    .atStartOfDay(); // 转换为当天0点的LocalDateTime

            Pageable pageable = PageRequest.of(0, RANKING_SIZE);
            // 此时传入的是LocalDateTime，匹配Repository方法
            List<BookEntity> newBooks = bookRepository.findNewBooksByDate(threeMonthsAgo, pageable);

            // 新书足够，直接返回
            if (newBooks.size() >= RANKING_SIZE) {
                return convertToDTOs(newBooks.stream()
                        .limit(RANKING_SIZE)
                        .collect(Collectors.toList()));
            }

            // 新书不足，用高评分书籍补足
            log.info("新书不足{}本，用高评分书籍补足", RANKING_SIZE);
            return supplementWithHighRatingBooks(newBooks);
        } catch (Exception e) {
            log.error("获取新书榜失败", e);
            return getFallbackRanking();
        }
    }

    @Override
    public List<SimpleBookDTO> getMasterpieceRanking() {
        try {
            // 获取所有已发布书籍
            List<BookEntity> allBooks = bookRepository.findAllPublishedBooks();

            // 筛选神作级书籍：高评分 + 高阅读人数 + 有历史
            List<BookScore> masterpieceBooks = allBooks.stream()
                    .filter(book -> {
                        Float rating = book.getRating();
                        Integer readCount = book.getReadCount();
                        LocalDateTime createdAt = book.getCreatedAt();

                        // 神作门槛
                        boolean isHighRating = Optional.ofNullable(rating).orElse(0.0f) >= 4.3f;
                        boolean isHighReadCount = Optional.ofNullable(readCount).orElse(0) >= 5000;
                        boolean hasHistory = Optional.ofNullable(createdAt)
                                .map(ct -> ChronoUnit.MONTHS.between(ct, LocalDateTime.now()) >= 3)
                                .orElse(false);

                        return isHighRating && isHighReadCount && hasHistory;
                    })
                    .map(book -> {
                        double score = calculateMasterpieceScore(book);
                        return new BookScore(book, score);
                    })
                    .sorted(Comparator.comparingDouble(BookScore::getScore).reversed())
                    .limit(RANKING_SIZE)
                    .collect(Collectors.toList());

            // 神作不足，降低标准补充
            if (masterpieceBooks.size() < RANKING_SIZE) {
                log.info("神作级书籍不足{}本，降低标准补充", RANKING_SIZE);
                masterpieceBooks = supplementMasterpieceBooks(masterpieceBooks, allBooks);
            }

            return convertToDTOs(
                    masterpieceBooks.stream()
                            .map(BookScore::getBook)
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("获取神作榜失败", e);
            return getFallbackRanking();
        }
    }

    /**
     * 神作榜评分算法
     * 权重：质量评分(60%) + 经典程度(30%) + 口碑传播(10%)
     */
    private double calculateMasterpieceScore(BookEntity book) {
        Float rating = Optional.ofNullable(book.getRating()).orElse(0.0f);
        Integer readCount = Optional.ofNullable(book.getReadCount()).orElse(0);
        LocalDateTime createdAt = book.getCreatedAt();

        double ratingValue = rating.doubleValue();
        int readCountValue = readCount;

        // 1. 质量评分
        double normalizedRating = normalize(ratingValue, 0.0, 5.0);

        // 2. 经典程度
        double classicFactor = calculateClassicFactor(book);

        // 3. 口碑传播
        double wordOfMouth = normalizeLog(readCountValue);

        // 神作榜权重公式
        double score = normalizedRating * 0.6 + // 质量评分 60%
                classicFactor * 0.3 + // 经典程度 30%
                wordOfMouth * 0.1; // 口碑传播 10%

        return Math.round(score * 100.0) / 100.0;
    }

    // ===================== 工具方法 =====================

    /**
     * 加载近期阅读统计数据（核心修改：适配read_date=date类型）
     */
    private void loadRecentReadStats(int days) {
        try {
            // 1. read_date是date类型 → 生成LocalDate参数
            LocalDate startDate = LocalDate.now().minusDays(days);
            List<Object[]> stats = bookRepository.findRecentReadStats(startDate);

            Map<Integer, RecentReadStats> statsMap = new HashMap<>();
            for (Object[] row : stats) {
                Integer bookId = (Integer) row[0];
                Long readCount = (Long) row[1];
                LocalDateTime latestReadTime = null;

                // 2. 数据库返回read_date是date → 转为LocalDateTime（补0点时分秒）
                if (row[2] != null) {
                    if (row[2] instanceof LocalDate) {
                        latestReadTime = ((LocalDate) row[2]).atStartOfDay();
                    } else if (row[2] instanceof LocalDateTime) {
                        latestReadTime = (LocalDateTime) row[2];
                    }
                }

                statsMap.put(bookId, new RecentReadStats(readCount, latestReadTime));
            }

            if (days <= 7) {
                weeklyReadStats = statsMap;
            } else {
                monthlyReadStats = statsMap;
            }
        } catch (Exception e) {
            log.warn("加载阅读统计数据失败: {}", e.getMessage());
        }
    }

    /**
     * 计算近期热度
     */
    private double calculateRecentHeat(Integer bookId, int days) {
        Map<Integer, RecentReadStats> stats = (days <= 7) ? weeklyReadStats : monthlyReadStats;

        if (stats != null && stats.containsKey(bookId)) {
            RecentReadStats bookStats = stats.get(bookId);
            long recentReads = bookStats.getReadCount();
            LocalDateTime latestRead = bookStats.getLatestReadTime();

            if (recentReads > 0 && latestRead != null) {
                // 热度 = 近期阅读次数 * 时间衰减因子
                double timeDecay = calculateTimeDecay(latestRead, days);
                return Math.log10(recentReads + 1) * timeDecay;
            }
        }

        return 0.0;
    }

    /**
     * 计算时间衰减因子
     */
    private double calculateTimeDecay(LocalDateTime lastTime, int periodDays) {
        long hoursSince = ChronoUnit.HOURS.between(lastTime, LocalDateTime.now());
        double decay = Math.max(0, 1.0 - (hoursSince / (periodDays * 24.0)));
        return Math.pow(decay, 0.7); // 减缓衰减
    }

    /**
     * 计算新鲜度因子（created_at是datetime → 保留LocalDateTime逻辑）
     */
    private double calculateFreshnessFactor(LocalDateTime createdAt, int newBookDays) {
        if (createdAt == null) {
            return 0.0;
        }

        long daysSinceCreation = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
        if (daysSinceCreation <= newBookDays) {
            return Math.max(0, 1.0 - (daysSinceCreation / (double) newBookDays));
        }
        return 0.0;
    }

    /**
     * 计算评分稳定性
     */
    private double calculateRatingStability(BookEntity book) {
        Float rating = book.getRating();
        if (rating == null || rating < 4.0f) {
            return 0.0;
        }

        // 高评分书籍稳定性更高
        if (rating >= 4.5f) {
            return 0.8;
        } else if (rating >= 4.3f) {
            return 0.6;
        } else if (rating >= 4.0f) {
            return 0.4;
        }
        return 0.0;
    }

    /**
     * 计算经典程度因子
     */
    private double calculateClassicFactor(BookEntity book) {
        double factor = 0.0;

        // 1. 高评分加分
        Float rating = book.getRating();
        if (rating != null) {
            if (rating >= 4.7f) {
                factor += 0.5;
            } else if (rating >= 4.5f) {
                factor += 0.3;
            } else if (rating >= 4.3f) {
                factor += 0.1;
            }
        }

        // 2. 高阅读人数加分
        Integer readCount = book.getReadCount();
        if (readCount != null) {
            if (readCount >= 10000) {
                factor += 0.3;
            } else if (readCount >= 5000) {
                factor += 0.2;
            } else if (readCount >= 1000) {
                factor += 0.1;
            }
        }

        // 3. 历史加分（超过6个月）
        LocalDateTime createdAt = book.getCreatedAt();
        if (createdAt != null && ChronoUnit.MONTHS.between(createdAt, LocalDateTime.now()) >= 6) {
            factor += 0.2;
        }

        // 因子上限为1.0
        return Math.min(factor, 1.0);
    }

    /**
     * 线性归一化（0-1区间）
     */
    private double normalize(double value, double min, double max) {
        if (max <= min) {
            return 0.0;
        }
        return Math.max(0, Math.min(1, (value - min) / (max - min)));
    }

    /**
     * 对数归一化（防止马太效应）
     */
    private double normalizeLog(int value) {
        if (value <= 0) {
            return 0.0;
        }
        // 最大参考值设为100万，归一化到0-1
        return Math.log10(value + 1) / Math.log10(1000000 + 1);
    }

    /**
     * 新书不足时用高评分书籍补足
     */
    private List<SimpleBookDTO> supplementWithHighRatingBooks(List<BookEntity> newBooks) {
        try {
            int needed = RANKING_SIZE - newBooks.size();
            Pageable pageable = PageRequest.of(0, needed);
            List<BookEntity> highRatingBooks = bookRepository.findBooksByRating(pageable);

            List<BookEntity> result = new ArrayList<>(newBooks);
            // 过滤重复书籍
            Set<Integer> newBookIds = newBooks.stream()
                    .map(BookEntity::getBookId)
                    .collect(Collectors.toSet());
            highRatingBooks.stream()
                    .filter(book -> !newBookIds.contains(book.getBookId()))
                    .forEach(result::add);

            return convertToDTOs(result.stream()
                    .distinct()
                    .limit(RANKING_SIZE)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.warn("补充高评分书籍失败: {}", e.getMessage());
            return convertToDTOs(newBooks);
        }
    }

    /**
     * 神作不足时降低标准补充
     */
    private List<BookScore> supplementMasterpieceBooks(
            List<BookScore> masterpieceBooks, List<BookEntity> allBooks) {

        int needed = RANKING_SIZE - masterpieceBooks.size();
        if (needed <= 0) {
            return masterpieceBooks;
        }

        // 已选书籍ID去重
        Set<Integer> existingIds = masterpieceBooks.stream()
                .map(bs -> bs.getBook().getBookId())
                .collect(Collectors.toSet());

        // 降低标准筛选
        List<BookScore> supplementary = allBooks.stream()
                .filter(book -> !existingIds.contains(book.getBookId()))
                .filter(book -> {
                    Float rating = Optional.ofNullable(book.getRating()).orElse(0.0f);
                    Integer readCount = Optional.ofNullable(book.getReadCount()).orElse(0);
                    // 降低标准：评分≥4.0 且 阅读人数≥1000
                    return rating >= 4.0f && readCount >= 1000;
                })
                .map(book -> new BookScore(book, calculateMasterpieceScore(book)))
                .sorted(Comparator.comparingDouble(BookScore::getScore).reversed())
                .limit(needed)
                .collect(Collectors.toList());

        // 合并结果
        List<BookScore> result = new ArrayList<>(masterpieceBooks);
        result.addAll(supplementary);
        return result.stream().limit(RANKING_SIZE).collect(Collectors.toList());
    }

    /**
     * 批量转换为DTO
     */
    private List<SimpleBookDTO> convertToDTOs(List<BookEntity> books) {
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 单个BookEntity转SimpleBookDTO
     */
    private SimpleBookDTO convertToDTO(BookEntity book) {
        SimpleBookDTO dto = new SimpleBookDTO();
        dto.setBookId(book.getBookId());
        dto.setBookTitle(book.getTitle());

        // 处理作者信息（解决类型不匹配问题）
        dto.setAuthor(getAuthorName(book.getAuthor()));

        dto.setCover(ImagePathUtils.processCoverPath(book.getCover()));
        dto.setReadingStatus("unread"); // 榜单书籍默认未读

        return dto;
    }

    /**
     * 安全获取作者名（避免空指针）
     */
    private String getAuthorName(AuthorEntity author) {
        if (author == null) {
            return "未知作者";
        }
        // 优先获取作者名称，兜底返回未知
        return Optional.ofNullable(author.getAuthorName()).orElse("未知作者");
    }

    /**
     * 降级方案：返回按阅读量排序的榜单
     */
    private List<SimpleBookDTO> getFallbackRanking() {
        try {
            Pageable pageable = PageRequest.of(0, RANKING_SIZE);
            List<BookEntity> books = bookRepository.findBooksByReadCount(pageable);
            return convertToDTOs(books);
        } catch (Exception e) {
            log.error("降级方案也失败", e);
            return Collections.emptyList();
        }
    }

    // ===================== 内部类 =====================

    /**
     * 书籍得分包装类
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class BookScore {
        private BookEntity book;
        private double score;
    }

    /**
     * 近期阅读统计数据
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RecentReadStats {
        private long readCount;
        private LocalDateTime latestReadTime;
    }
}