package com.weread.service.impl.user;

import com.weread.dto.user.UpdateProfileDTO;
import com.weread.entity.community.PostEntity;
import com.weread.entity.user.FollowEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.UserRepository;
import com.weread.repository.community.CommentRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.repository.ReadingProgressRepository;
import com.weread.repository.note.NoteRepository;
import com.weread.repository.asset.MemberCardRepository;
import com.weread.service.user.UserService;
import com.weread.vo.user.FollowListVO;
import com.weread.vo.user.FollowUserVO;
import com.weread.vo.user.UserProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final NoteRepository noteRepository;
    private final ReadingProgressRepository readingProgressRepository;
    private final MemberCardRepository memberCardRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserProfileVO getUserHome(Integer userId) {
        // 获取用户基本信息
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
    
        // 构建UserProfileVO
        UserProfileVO profileVO = new UserProfileVO();
    
        // 设置用户ID
        profileVO.setUserId(user.getUserId());
    
        // 确保所有String字段不为null
        profileVO.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
        profileVO.setUsername(user.getUsername() != null ? user.getUsername() : "");
        profileVO.setBio(user.getBio() != null ? user.getBio() : "");
    
        // 确保Integer字段不为null
        profileVO.setCoinCount(user.getCoins() != null ? user.getCoins() : 0);
    
        // 会员状态 - 确保Boolean不为null
        if (user.getMembershipExpireAt() != null) {
            boolean isMember = ((LocalDateTime) user.getMembershipExpireAt()).isAfter(LocalDateTime.now());
            profileVO.setIsMember(isMember);
        } else {
            profileVO.setIsMember(false);
        }
    
        // 计算会员剩余天数 - 确保Integer不为null
        if (user.getMembershipExpireAt() != null) {
            long days = ChronoUnit.DAYS.between(LocalDateTime.now(), (Temporal) user.getMembershipExpireAt());
            profileVO.setMemberExpireDays(days > 0 ? (int) days : 0);
        } else {
            profileVO.setMemberExpireDays(0);
        }
    
        // 统计数据 - 确保所有Integer字段不为null
        Integer followingCount = getFollowingCount(userId);
        profileVO.setFollowingCount(followingCount != null ? followingCount : 0);
    
        Integer followerCount = getFollowerCount(userId);
        profileVO.setFollowerCount(followerCount != null ? followerCount : 0);
    
        Integer postCount = getPostCount(userId);
        profileVO.setPostCount(postCount != null ? postCount : 0);
    
        profileVO.setExperienceCardCount(getMemberCardCount(userId));
    
        // 阅读统计
        UserProfileVO.ReadingStatsVO readingStats = getReadingStats(userId);
        profileVO.setReadingStats(readingStats);
    
        // 连续阅读天数
        Integer consecutiveDays = getConsecutiveReadingDays(userId);
        profileVO.setConsecutiveReadingDays(consecutiveDays != null ? consecutiveDays : 0);
    
        return profileVO;
    }
    
    @Override
    @Transactional
    public UserProfileVO updateUserProfile(Integer userId, UpdateProfileDTO updateDTO) {
        // 获取用户
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        
        // 检查用户名是否已被占用（如果用户名有修改）
        if (updateDTO.getUsername() != null && 
            !updateDTO.getUsername().equals(user.getUsername())) {
            boolean exists = userRepository.existsByUsername(updateDTO.getUsername());
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名已被占用");
            }
            user.setUsername(updateDTO.getUsername());
        }
        
        // 更新其他字段
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        
        if (updateDTO.getBio() != null) {
            user.setBio(updateDTO.getBio());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // 返回更新后的信息
        return getUserHome(userId);
    }
    
    @Override
    @Transactional
    public void updateLastLoginTime(Integer userId) {
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
    }
    
    // ============ 私有方法，需要根据实际情况实现 ============
    
    private Integer getFollowingCount(Integer userId) {
        return followRepository.countByFollowerId(userId);
    }
    
    private Integer getFollowerCount(Integer userId) {
        return followRepository.countByFollowingId(userId);
    }
    
    private Integer getPostCount(Integer userId) {
        return postRepository.countByAuthorId(userId);
    }
    
    private Integer getMemberCardCount(Integer userId) {
        // 统计用户未使用的有效体验卡数量（按天数计算）
        LocalDateTime now = LocalDateTime.now();
        List<com.weread.entity.asset.MemberCardEntity> unusedCards = 
            memberCardRepository.findUnusedValidCardsByUserId(userId, now);
        
        // 计算总天数：将所有未使用的有效体验卡的天数相加
        int totalDays = unusedCards.stream()
            .mapToInt(card -> card.getDurationDays() != null ? card.getDurationDays() : 0)
            .sum();
        
        return totalDays;
    }
    
    private UserProfileVO.ReadingStatsVO getReadingStats(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        
        // 计算时间范围：本周、本月、今年
        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        LocalDateTime weekStart = startOfWeek.atStartOfDay();
        LocalDateTime weekEnd = endOfWeek.atTime(23, 59, 59);
        
        LocalDate startOfMonth = today.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
        LocalDateTime monthStart = startOfMonth.atStartOfDay();
        LocalDateTime monthEnd = endOfMonth.atTime(23, 59, 59);
        
        LocalDate startOfYear = today.with(java.time.temporal.TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = today.with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
        LocalDateTime yearStart = startOfYear.atStartOfDay();
        LocalDateTime yearEnd = endOfYear.atTime(23, 59, 59);
        
        UserProfileVO.ReadingStatsVO stats = new UserProfileVO.ReadingStatsVO();
        
        // 1. 阅读时长统计 - 从UserEntity获取
        UserEntity user = userRepository.findById(userId).orElse(new UserEntity());
        Integer totalReadingTime = user.getTotalReadingTime() != null ? user.getTotalReadingTime() : 0;
        
        // 假设阅读时长分布：按阅读书籍比例分配
        Integer totalBooksRead = getTotalBooksRead(userId);
        if (totalBooksRead > 0) {
            Integer avgTimePerBook = totalReadingTime / totalBooksRead;
            
            Integer weeklyBooks = getBooksReadByPeriod(userId, weekStart, weekEnd);
            stats.setWeeklyReadingTime(weeklyBooks * avgTimePerBook);
            
            Integer monthlyBooks = getBooksReadByPeriod(userId, monthStart, monthEnd);
            stats.setMonthlyReadingTime(monthlyBooks * avgTimePerBook);
            
            Integer yearlyBooks = getBooksReadByPeriod(userId, yearStart, yearEnd);
            stats.setYearlyReadingTime(yearlyBooks * avgTimePerBook);
        } else {
            stats.setWeeklyReadingTime(0);
            stats.setMonthlyReadingTime(0);
            stats.setYearlyReadingTime(0);
        }
        stats.setTotalReadingTime(totalReadingTime);
        
        // 2. 阅读书籍统计
        stats.setWeeklyBooksRead(getBooksReadByPeriod(userId, weekStart, weekEnd));
        stats.setMonthlyBooksRead(getBooksReadByPeriod(userId, monthStart, monthEnd));
        stats.setYearlyBooksRead(getBooksReadByPeriod(userId, yearStart, yearEnd));
        stats.setTotalBooksRead(totalBooksRead);
        
        // 3. 读完书籍统计
        stats.setWeeklyBooksFinished(getBooksFinishedByPeriod(userId, weekStart, weekEnd));
        stats.setMonthlyBooksFinished(getBooksFinishedByPeriod(userId, monthStart, monthEnd));
        stats.setYearlyBooksFinished(getBooksFinishedByPeriod(userId, yearStart, yearEnd));
        stats.setTotalBooksFinished(getTotalBooksFinished(userId));
        
        // 4. 笔记统计
        stats.setWeeklyNoteCount(countNotesByPeriod(userId, weekStart, weekEnd));
        stats.setMonthlyNoteCount(countNotesByPeriod(userId, monthStart, monthEnd));
        stats.setYearlyNoteCount(countNotesByPeriod(userId, yearStart, yearEnd));
        stats.setTotalNoteCount(countTotalNotes(userId));
        
        return stats;
    }
    
    // 辅助方法
    private Integer getTotalBooksRead(Integer userId) {
        Integer count = readingProgressRepository.countTotalBooksRead(userId);
        return count != null ? count : 0;
    }
    
    private Integer getTotalBooksFinished(Integer userId) {
        Integer count = readingProgressRepository.countBooksFinished(userId);
        return count != null ? count : 0;
    }
    
    private Integer getBooksReadByPeriod(Integer userId, LocalDateTime start, LocalDateTime end) {
        Integer count = readingProgressRepository.countBooksReadByPeriod(userId, start, end);
        return count != null ? count : 0;
    }
    
    private Integer getBooksFinishedByPeriod(Integer userId, LocalDateTime start, LocalDateTime end) {
        Integer count = readingProgressRepository.countBooksFinishedByPeriod(userId, start, end);
        return count != null ? count : 0;
    }
    
    private Integer countNotesByPeriod(Integer userId, LocalDateTime start, LocalDateTime end) {
        // 如果有NoteRepository
        if (noteRepository != null) {
            Integer count = noteRepository.countByUserIdAndCreatedAtBetween(userId, start, end);
            return count != null ? count : 0;
        }
        return 0;
    }
    
    private Integer countTotalNotes(Integer userId) {
        if (noteRepository != null) {
            Integer count = noteRepository.countByUserId(userId);
            return count != null ? count : 0;
        }
        return 0;
    }
    
    private Integer getConsecutiveReadingDays(Integer userId) {
        // 直接计算，避免复杂SQL
        LocalDate today = LocalDate.now();
        int consecutiveDays = 0;
    
        // 检查最多30天
        for (int i = 0; i < 30; i++) {
            LocalDate checkDate = today.minusDays(i);
            LocalDateTime startOfDay = checkDate.atStartOfDay();
            LocalDateTime endOfDay = checkDate.atTime(23, 59, 59);
        
            // 查询这一天是否有阅读记录
            Integer count = readingProgressRepository.countBooksReadByPeriod(
                userId, startOfDay, endOfDay);
        
            if (count != null && count > 0) {
                consecutiveDays++;
            }else if (i == 0) {
                // 今天没读，继续检查昨天，不算中断
                continue;
            } else {
                // 出现中断
                break;
            }
        }    
        return consecutiveDays;
    }
    
    

    @Override
    @Transactional
    public void followUser(Integer followerId, Integer followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能关注自己。");
        }
        
        // 1. 验证目标用户是否存在
        if (!userRepository.existsById(followingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "目标用户不存在。");
        }

        // 2. 检查是否已关注
        if (followRepository.findByFollowerIdAndFollowingId(followerId, followingId).isPresent()) {
            return; // 已关注，无需操作
        }

        // 3. 保存新的 FollowEntity
        FollowEntity follow = new FollowEntity();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        followRepository.save(follow);
        
        // 【✅ 实际更新操作】: 更新 UserEntity 中的 followingCount/followerCount 计数
        userRepository.incrementFollowingCount(followerId); // 关注者 (follower) 的关注数 +1
        userRepository.incrementFollowerCount(followingId); // 被关注者 (following) 的粉丝数 +1
    }
    // =========================================================
    // 【实现 2】: 取消关注用户
    // =========================================================

    @Override
    @Transactional // 必须要有 @Transactional
    public void unfollowUser(Integer followerId, Integer followingId) {
    
        // 1. 查找关注记录
        Optional<FollowEntity> existingFollow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
    
        if (existingFollow.isPresent()) {
            // 2. 删除关注记录
            followRepository.delete(existingFollow.get());
        
            // 【✅ 实际更新操作】: 更新 UserEntity 中的 followingCount/followerCount 计数
        
            // 3. 关注者 (follower) 的关注数 -1
            userRepository.decrementFollowingCount(followerId); 
        
            // 4. 被关注者 (following) 的粉丝数 -1
            userRepository.decrementFollowerCount(followingId);
        }
        // 如果不存在，方法结束，无需操作
    }
    @Override
    public FollowListVO getFollowers(Integer userId, int page, int limit, Integer currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 查找目标用户（userId）的粉丝列表 (即 FollowingId = userId)
        Page<FollowEntity> followPage = followRepository.findByFollowingId(userId, pageable);
        
        return buildFollowListResponse(followPage, currentUserId, true);
    }

    @Override
    public FollowListVO getFollowings(Integer userId, int page, int limit, Integer currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 查找目标用户（userId）的关注列表 (即 FollowerId = userId)
        Page<FollowEntity> followPage = followRepository.findByFollowerId(userId, pageable);
        
        return buildFollowListResponse(followPage, currentUserId, false);
    }



    /**
     * 辅助方法：将 FollowEntity 列表转换为 FollowUserVO 列表
     * @param followPage 包含 FollowEntity 的分页结果
     * @param currentUserId 当前登录用户ID (用于判断是否互相关注)
     * @param isFollowersList true表示正在查询粉丝列表 (FollowingId是目标用户)，false表示查询关注列表 (FollowerId是目标用户)
     */
    private FollowListVO buildFollowListResponse(Page<FollowEntity> followPage, Integer currentUserId, boolean isFollowersList) {
    
        List<Integer> targetUserIds = followPage.getContent().stream()
            .map(entity -> isFollowersList ? entity.getFollowerId() : entity.getFollowingId())
            .collect(Collectors.toList());

        // 1. 批量查询目标用户信息
        List<UserEntity> targetUsers = userRepository.findAllById(targetUserIds);
    
        // 2. 批量查询当前用户与列表中用户的关注关系
        List<FollowUserVO> followUserVOs = targetUsers.stream()
            .map(userEntity -> {
                FollowUserVO vo = convertToFollowUserVO(userEntity);
            
                // 设置默认值
                vo.setIsFollowing(false);
                vo.setIsFollower(false);
            
                if (currentUserId != null) {
                    // a. 检查当前用户是否关注了目标用户 (isFollowing)
                    boolean isFollowing = followRepository.findByFollowerIdAndFollowingId(
                        currentUserId, userEntity.getUserId()).isPresent();
                    vo.setIsFollowing(isFollowing);
                    
                    // b. 检查目标用户是否关注了当前用户 (isFollower)
                    boolean isFollower = followRepository.findByFollowerIdAndFollowingId(
                        userEntity.getUserId(), currentUserId).isPresent();
                    vo.setIsFollower(isFollower);
                }
            
                return vo;
            })
            .collect(Collectors.toList());

        FollowListVO response = new FollowListVO();
        response.setUsers(followUserVOs);
        response.setTotalElements(followPage.getTotalElements());
        response.setTotalPages(followPage.getTotalPages());
        return response;
    }

    /**
     * 辅助方法：将 UserEntity 转换为 FollowUserVO
     */
    private FollowUserVO convertToFollowUserVO(UserEntity userEntity) {
        FollowUserVO vo = new FollowUserVO();
        vo.setUserId(userEntity.getUserId());
        vo.setUsername(userEntity.getUsername() != null ? userEntity.getUsername() : "");
        vo.setAvatar(userEntity.getAvatar() != null ? userEntity.getAvatar() : "");
        vo.setBio(userEntity.getBio() != null ? userEntity.getBio() : "");
    
        // 设置默认值
        vo.setIsFollowing(false);
        vo.setIsFollower(false);
    
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserPosts(Integer userId, Integer cursor, Integer limit) {
        log.info("获取用户 {} 的帖子列表，cursor: {}, limit: {}", userId, cursor, limit);
        
        // 参数验证和默认值
        if (limit == null || limit < 1) {
            limit = 20;
        }
        if (limit > 50) {
            limit = 50;
        }
        
        // 1. 获取帖子列表（基于游标分页）
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        
        List<PostEntity> posts;
        if (cursor != null) {
            // 基于游标查询
            Integer cursorId = cursor;
            posts = postRepository.findUserPostsAfterCursor(userId, cursorId, pageable);
        } else {
            // 第一次查询
            posts = postRepository.findUserPosts(userId, pageable);
        }
        
        // 2. 获取统计数据
        Integer postCount = postRepository.countByUserId(userId);
        Integer commentCount = commentRepository.countByUserId(userId);
        Integer likeCount = likeRepository.countByUserId(userId);
        
        // 3. 转换为响应格式
        List<Map<String, Object>> postList = posts.stream()
            .map(post -> convertPostToMap(post, userId))
            .collect(Collectors.toList());
        
        // 4. 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("posts", postList);
        result.put("hasMore", postList.size() >= limit);
        result.put("nextCursor", postList.isEmpty() ? null : 
            (Integer) postList.get(postList.size() - 1).get("postId"));
        result.put("postCount", postCount);
        result.put("commentCount", commentCount);
        result.put("likeCount", likeCount);
        
        return result;
    }
    
    private Map<String, Object> convertPostToMap(PostEntity post, Integer currentUserId) {
        Map<String, Object> map = new HashMap<>();
        map.put("postId", post.getPostId());
        map.put("postTitle", post.getTitle() != null ? post.getTitle() : "");
        map.put("content", post.getContent() != null ? post.getContent() : "");
        map.put("publishTime", post.getCreatedAt());
        
        // 获取作者信息
        try {
            Optional<UserEntity> authorOpt = userRepository.findById(post.getAuthorId());
            if (authorOpt.isPresent()) {
                UserEntity author = authorOpt.get();
                map.put("authorName", author.getUsername() != null ? author.getUsername() : "");
                map.put("authorAvatar", author.getAvatar() != null ? author.getAvatar() : "");
                
                // 检查当前用户是否关注作者（自己关注自己为false）
                boolean isFollowing = false;
                if (currentUserId != null && !currentUserId.equals(post.getAuthorId())) {
                    isFollowing = followRepository.findByFollowerIdAndFollowingId(currentUserId, post.getAuthorId())
                            .isPresent();
                }
                map.put("isFollowingAuthor", isFollowing);
            } else {
                map.put("authorName", "");
                map.put("authorAvatar", "");
                map.put("isFollowingAuthor", false);
            }
        } catch (Exception e) {
            log.warn("获取作者信息失败: authorId={}", post.getAuthorId(), e);
            map.put("authorName", "");
            map.put("authorAvatar", "");
            map.put("isFollowingAuthor", false);
        }
        
        // 获取统计信息
        try {
            Integer commentCount = commentRepository.countByPostId(post.getPostId());
            Integer likeCount = likeRepository.countByPostId(post.getPostId());
            map.put("commentCount", commentCount != null ? commentCount : 0);
            map.put("likeCount", likeCount != null ? likeCount : 0);
        } catch (Exception e) {
            log.warn("获取统计信息失败: postId={}", post.getPostId(), e);
            map.put("commentCount", 0);
            map.put("likeCount", 0);
        }
        
        // 检查是否点赞
        boolean isLiked = false;
        if (currentUserId != null) {
            try {
                isLiked = likeRepository.findByPostIdAndUserId(post.getPostId(), currentUserId).isPresent();
            } catch (Exception e) {
                log.warn("检查点赞状态失败: postId={}, userId={}", post.getPostId(), currentUserId, e);
            }
        }
        map.put("isLiked", isLiked);
        
        // 获取提到的书籍
        try {
            List<com.weread.entity.book.BookEntity> relatedBooks = post.getRelatedBooks();
            if (relatedBooks != null && !relatedBooks.isEmpty()) {
                List<Map<String, Object>> mentionedBooks = relatedBooks.stream()
                    .map(book -> {
                        Map<String, Object> bookMap = new HashMap<>();
                        bookMap.put("bookId", book.getBookId());
                        bookMap.put("bookTitle", book.getTitle() != null ? book.getTitle() : "");
                        bookMap.put("cover", book.getCover() != null ? book.getCover() : "");
                        if (book.getAuthor() != null) {
                            bookMap.put("authorName", book.getAuthor().getAuthorName() != null ? 
                                book.getAuthor().getAuthorName() : "");
                        } else {
                            bookMap.put("authorName", "");
                        }
                        return bookMap;
                    })
                    .collect(Collectors.toList());
                map.put("mentionedBooks", mentionedBooks);
            } else {
                map.put("mentionedBooks", java.util.Collections.emptyList());
            }
        } catch (Exception e) {
            log.warn("获取相关书籍失败: postId={}", post.getPostId(), e);
            map.put("mentionedBooks", java.util.Collections.emptyList());
        }
        
        return map;
    }
}