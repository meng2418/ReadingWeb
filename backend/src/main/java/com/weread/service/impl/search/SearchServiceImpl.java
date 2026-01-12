package com.weread.service.impl.search;

import com.weread.dto.search.AuthorSearchResultDto;
import com.weread.dto.book.SimpleBookDTO;
import com.weread.dto.search.SearchResponseDto;
import com.weread.entity.book.BookEntity;
import com.weread.entity.author.AuthorEntity;
import com.weread.repository.book.BookRepository;
import com.weread.repository.author.AuthorRepository;
import com.weread.service.search.SearchService;
import com.weread.util.ImagePathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public SearchResponseDto search(String keyword) {
        SearchResponseDto response = new SearchResponseDto();

        if (!StringUtils.hasText(keyword)) {
            response.setBooks(List.of());
            response.setAuthors(List.of());
            return response;
        }

        // 修改这里：使用 JOIN FETCH 方法
        List<SimpleBookDTO> books = bookRepository
                .findByTitleContainingIgnoreCaseWithAuthor(keyword) // 使用新方法
                .stream()
                .map(this::convertToSimpleBookDTO)
                .collect(Collectors.toList());

        // 模糊搜索作者（根据姓名）
        List<AuthorSearchResultDto> authors = authorRepository
                .findByAuthorNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToAuthorSearchResultDto)
                .collect(Collectors.toList());

        response.setBooks(books);
        response.setAuthors(authors);

        return response;
    }

    @Override
    public List<SimpleBookDTO> searchBooks(String keyword) {
        // 如果还需要单独的书籍搜索，也使用新方法
        return bookRepository.findByTitleContainingIgnoreCaseWithAuthor(keyword)
                .stream()
                .map(this::convertToSimpleBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthorSearchResultDto> searchAuthors(String keyword) {
        return authorRepository.findByAuthorNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToAuthorSearchResultDto)
                .collect(Collectors.toList());
    }

    private SimpleBookDTO convertToSimpleBookDTO(BookEntity book) {
        SimpleBookDTO dto = new SimpleBookDTO();
        dto.setBookId(book.getBookId());
        dto.setBookTitle(book.getTitle());
        dto.setCover(ImagePathUtils.processCoverPath(book.getCover()));

        // 现在 author 已经通过 JOIN FETCH 加载，可以直接访问
        dto.setAuthorName(book.getAuthor() != null ? book.getAuthor().getAuthorName() : "未知作者");

        dto.setRating(book.getRating());
        dto.setReadCount(book.getReadCount());
        return dto;
    }

    private AuthorSearchResultDto convertToAuthorSearchResultDto(AuthorEntity author) {
        AuthorSearchResultDto result = new AuthorSearchResultDto();
        result.setAuthorId(author.getAuthorId());
        result.setAvatar(author.getAvatar());
        result.setAuthorName(author.getAuthorName());
        result.setAuthorBio(author.getBio());

        // 设置默认值
        result.setFollowerCount(0);
        result.setRepresentativeWorks(List.of("暂无代表作"));

        return result;
    }
}