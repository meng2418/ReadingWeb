package com.weread.service.impl.author;

import com.weread.entity.AuthorEntity;
import com.weread.entity.BookEntity;
import com.weread.repository.AuthorRepository;
import com.weread.repository.BookRepository;
import com.weread.repository.author.UserFollowAuthorRepository;
import com.weread.service.author.AuthorService;
import com.weread.vo.author.AuthorDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final UserFollowAuthorRepository userFollowAuthorRepository;
    
    @Override
    @Transactional(readOnly = true)
    public AuthorDetailVO getAuthorDetail(Integer authorId, Integer currentUserId) {
        // 1. 查询作者信息
        AuthorEntity author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("作者不存在，ID: " + authorId));
        
        // 2. 查询作者的所有作品
        List<BookEntity> books = bookRepository.findByAuthorIdAndIsPublishedTrue(authorId);
        
        // 3. 统计作品数量
        int workCount = bookRepository.countByAuthorIdAndIsPublishedTrue(authorId);
        
        // 4. 构建响应对象
        AuthorDetailVO vo = new AuthorDetailVO();
        vo.setAuthorId(author.getAuthorId());
        vo.setAuthorName(author.getAuthorName());
        vo.setAuthorBio(author.getBio() != null ? author.getBio() : "");
        vo.setWorkCount(workCount);
        
        // 5. 转换作品列表
        List<AuthorDetailVO.AuthorWorkVO> works = books.stream()
                .map(this::convertToWorkVO)
                .collect(Collectors.toList());
        vo.setWorks(works);
        
        // 6. 设置关注相关字段
        // 统计作者的关注者数量
        int followerCount = userFollowAuthorRepository.countByAuthorId(authorId);
        vo.setFollowerCount(followerCount);
        
        // 检查当前用户是否关注该作者
        boolean isFollowing = false;
        if (currentUserId != null) {
            isFollowing = userFollowAuthorRepository.findByUserIdAndAuthorId(currentUserId, authorId).isPresent();
        }
        vo.setIsFollowing(isFollowing);
        
        return vo;
    }
    
    /**
     * 将 BookEntity 转换为 AuthorWorkVO
     */
    private AuthorDetailVO.AuthorWorkVO convertToWorkVO(BookEntity book) {
        AuthorDetailVO.AuthorWorkVO workVO = new AuthorDetailVO.AuthorWorkVO();
        workVO.setBookId(book.getBookId());
        workVO.setBookTitle(book.getTitle());
        workVO.setAuthorName(book.getAuthor() != null ? book.getAuthor().getAuthorName() : "");
        workVO.setCover(book.getCover() != null ? book.getCover() : "");
        workVO.setRating(book.getRating() != null ? book.getRating().intValue() : 0);
        workVO.setReadCount(book.getReadCount() != null ? book.getReadCount() : 0);
        workVO.setDescription(book.getDescription() != null ? book.getDescription() : "");
        return workVO;
    }
}

