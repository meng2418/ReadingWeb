package com.weread.service.impl.note;

import com.weread.dto.note.BookNoteDTO;
import com.weread.dto.note.BookNoteResponseDTO;
import com.weread.dto.note.ChapterNoteResponseDTO;
import com.weread.dto.note.NoteResponseDTO;
import com.weread.dto.note.UserNoteDTO;
import com.weread.dto.note.UserNotesResponseDTO;
import com.weread.entity.BookEntity;
import com.weread.entity.note.NoteEntity;
import com.weread.vo.user.HighlightVO;
import com.weread.repository.BookRepository;
import com.weread.repository.book.BookChapterRepository;
import com.weread.repository.note.NoteRepository;
import com.weread.service.note.NoteService;
import com.weread.vo.note.NoteVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final BookRepository bookRepository;
    private final BookChapterRepository chapterRepository;

    public NoteServiceImpl(NoteRepository noteRepository, BookRepository bookRepository, BookChapterRepository chapterRepository) {
        this.noteRepository = noteRepository;
        this.bookRepository = bookRepository;
        this.chapterRepository = chapterRepository;
    }

    @Override
    @Transactional
    public NoteEntity createNote(Integer userId, NoteEntity newNote) {
        if (newNote.getBookId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "书籍ID不能为空。");
        }
        newNote.setUserId(userId);
        newNote.setCreatedAt(LocalDateTime.now());
        
        // 自动设置默认值和非空约束
        if (newNote.getIsPublic() == null) newNote.setIsPublic(false);
        if (newNote.getType() == null) newNote.setType("highlight");

        return noteRepository.save(newNote);
    }

    @Override
    @Transactional
    public NoteEntity updateNote(Integer noteId, Integer userId, String content, Boolean isPublic, String color) {
        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "笔记不存在。"));

        // 检查权限
        if (!note.getUserId().equals(userId)) {
            logger.warn("用户 {} 尝试修改非自己的笔记 {}", userId, noteId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改他人笔记。");
        }

        // 更新内容
        if (content != null) {
            note.setContent(content);
        }
        if (isPublic != null) {
            note.setIsPublic(isPublic);
        }
        if (color != null) {
            note.setColor(color);
        }
        
        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public void deleteNote(Integer noteId, Integer userId) {
        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "笔记不存在。"));
        
        // 检查权限
        if (!note.getUserId().equals(userId)) {
            logger.warn("用户 {} 尝试删除非自己的笔记 {}", userId, noteId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除他人笔记。");
        }
        
        noteRepository.delete(note);
    }

    // 辅助转换方法 (您需要自行创建 NoteVO)
    private NoteVO convertToNoteVO(NoteEntity entity) {
        // 实际应用中，这里应使用 Mapper 转换
        NoteVO vo = new NoteVO();
        vo.setNoteId(entity.getNoteId());
        vo.setContent(entity.getContent());
        vo.setIsPublic(entity.getIsPublic());
        vo.setCreatedAt(entity.getCreatedAt());
        // ... set other fields
        return vo;
    }
    
    @Override
    public Page<NoteVO> getUserNotes(Integer userId, Pageable pageable) {
        Page<NoteEntity> entities = noteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return entities.map(this::convertToNoteVO);
    }

    @Override
    public List<NoteVO> getPublicChapterNotes(Integer chapterId) {
        // 获取某一章节的所有笔记，然后过滤出公开的，并转换
        List<NoteEntity> entities = noteRepository.findByChapterIdOrderByCreatedAtDesc(chapterId);
        
        return entities.stream()
                .filter(NoteEntity::getIsPublic) // 仅显示公开笔记
                .map(this::convertToNoteVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NoteResponseDTO createNoteFromDTO(Integer userId, Integer bookId, Integer chapterId,
                                              String quote, String lineType, String thought) {
        // 验证书籍是否存在
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "书籍不存在"));

        // 验证lineType
        if (lineType != null && !lineType.matches("marker|wavy|underline")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "划线类型无效，必须是 marker、wavy 或 underline");
        }

        // 创建笔记实体
        NoteEntity note = new NoteEntity();
        note.setUserId(userId);
        note.setBookId(bookId);
        note.setChapterId(chapterId);
        note.setContent(quote); // 将quote存储在content字段中
        note.setType("highlight"); // 默认类型
        note.setIsPublic(false); // 默认不公开
        note.setCreatedAt(LocalDateTime.now());
        
        // 将lineType映射到color字段（或者可以扩展实体添加lineType字段）
        // 这里暂时使用color字段存储lineType信息
        note.setColor(lineType != null ? lineType : "marker");

        note = noteRepository.save(note);

        // 构建响应DTO
        NoteResponseDTO response = new NoteResponseDTO();
        response.setNoteId(note.getNoteId());
        response.setQuote(quote);
        response.setLineType(lineType != null ? lineType : "marker");
        response.setNoteContent(thought != null ? thought : ""); // thought暂时返回，如果数据库不支持可以后续扩展
        response.setCreatedAt(note.getCreatedAt());

        return response;
    }

    @Override
    public List<ChapterNoteResponseDTO> getChapterNotes(Integer userId, Integer bookId, Integer chapterId) {
        // 查询指定用户、指定书籍和章节的笔记
        List<NoteEntity> entities = noteRepository.findByUserIdAndBookIdAndChapterIdOrderByCreatedAtDesc(
                userId, bookId, chapterId);
        
        return entities.stream()
                .map(this::convertToChapterNoteResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将NoteEntity转换为ChapterNoteResponseDTO
     */
    private ChapterNoteResponseDTO convertToChapterNoteResponseDTO(NoteEntity entity) {
        ChapterNoteResponseDTO dto = new ChapterNoteResponseDTO();
        dto.setNoteId(entity.getNoteId());
        dto.setQuote(entity.getContent()); // content字段存储quote
        dto.setStartIndex(0); // 数据库中没有此字段，使用默认值0
        dto.setEndIndex(entity.getContent() != null ? entity.getContent().length() : 0); // 使用内容长度作为endIndex
        // lineType从color字段解析，如果是数组格式则解析，否则转换为数组
        if (entity.getColor() != null) {
            // 如果color字段包含多个值（用逗号分隔），则分割；否则作为单个元素
            if (entity.getColor().contains(",")) {
                dto.setLineType(Arrays.asList(entity.getColor().split(",")));
            } else {
                dto.setLineType(Arrays.asList(entity.getColor()));
            }
        } else {
            dto.setLineType(Arrays.asList("marker")); // 默认值
        }
        dto.setThought(""); // 数据库中没有thought字段，使用空字符串
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPageNumber(entity.getPage() != null ? entity.getPage() : 0); // page字段映射到pageNumber
        return dto;
    }

    @Override
    public List<BookNoteResponseDTO> getBookNotes(Integer userId, Integer bookId) {
        // 查询指定用户、指定书籍的所有笔记
        List<NoteEntity> entities = noteRepository.findByUserIdAndBookIdOrderByCreatedAtDesc(userId, bookId);
        
        // 获取书籍信息
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "书籍不存在"));
        
        // 转换为DTO
        return entities.stream()
                .map(entity -> convertToBookNoteResponseDTO(entity, book))
                .collect(Collectors.toList());
    }

    /**
     * 将NoteEntity转换为BookNoteResponseDTO
     */
    private BookNoteResponseDTO convertToBookNoteResponseDTO(NoteEntity entity, BookEntity book) {
        BookNoteResponseDTO dto = new BookNoteResponseDTO();
        dto.setNoteId(entity.getNoteId());
        dto.setBookId(entity.getBookId());
        dto.setBookTitle(book.getTitle());
        dto.setChapterId(entity.getChapterId());
        
        // 获取章节名称
        if (entity.getChapterId() != null) {
            chapterRepository.findById(entity.getChapterId())
                    .ifPresent(chapter -> dto.setChapterName(chapter.getTitle()));
        }
        if (dto.getChapterName() == null) {
            dto.setChapterName("");
        }
        
        // 根据type字段判断noteType
        // "highlight" 对应 "line"，其他可能对应 "thought"
        if ("highlight".equals(entity.getType())) {
            dto.setNoteType("line");
        } else {
            dto.setNoteType("thought");
        }
        
        dto.setQuote(entity.getContent()); // content字段存储quote
        dto.setStartIndex(0); // 数据库中没有此字段，使用默认值0
        dto.setEndIndex(entity.getContent() != null ? entity.getContent().length() : 0); // 使用内容长度作为endIndex
        dto.setPageNumber(entity.getPage() != null ? entity.getPage() : 0); // page字段映射到pageNumber
        
        // lineType从color字段获取
        dto.setLineType(entity.getColor() != null ? entity.getColor() : "marker");
        
        // thought字段：数据库中没有单独存储，暂时使用空字符串
        // 如果后续需要支持，可以在NoteEntity中添加thought字段
        dto.setThought("");
        
        dto.setNoteCreatedAt(entity.getCreatedAt());
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserNotesResponseDTO getUserNotesWithCursor(Integer userId, Integer cursor, Integer limit) {
        // 参数验证和默认值设置
        if (limit == null || limit <= 0) {
            limit = 20; // 默认20条
        }
        if (limit > 50) {
            limit = 50; // 最大50条
        }

        // 创建分页对象（limit + 1，用于判断是否有更多数据）
        PageRequest pageable = PageRequest.of(0, limit + 1);

        // 查询笔记列表
        List<NoteEntity> notes;
        if (cursor == null) {
            // 第一页
            notes = noteRepository.findUserNotesFirstPage(userId, pageable);
        } else {
            // 后续页
            notes = noteRepository.findUserNotesByCursor(userId, cursor, pageable);
        }

        // 判断是否有更多数据
        boolean hasMore = notes.size() > limit;
        if (hasMore) {
            notes = notes.subList(0, limit); // 只取前limit条
        }

        // 转换为DTO
        List<UserNoteDTO> noteDTOs = notes.stream()
                .map(this::convertToUserNoteDTO)
                .collect(Collectors.toList());

        // 计算nextCursor（如果有更多数据，使用最后一条笔记的noteId）
        // 根据接口定义，nextCursor是必填字段，类型为string，不能为null
        String nextCursor = "";
        if (hasMore && !notes.isEmpty()) {
            nextCursor = String.valueOf(notes.get(notes.size() - 1).getNoteId());
        }

        // 获取总笔记数
        Integer totalCount = noteRepository.countByUserId(userId);

        // 构建响应DTO
        UserNotesResponseDTO response = new UserNotesResponseDTO();
        response.setNotes(noteDTOs);
        response.setHasMore(hasMore);
        response.setNextCursor(nextCursor); // 确保是string类型，不能为null
        response.setNoteCount(totalCount != null ? totalCount.intValue() : 0);

        return response;
    }

    /**
     * 将NoteEntity转换为UserNoteDTO
     */
    private UserNoteDTO convertToUserNoteDTO(NoteEntity entity) {
        UserNoteDTO dto = new UserNoteDTO();
        dto.setMarkId(entity.getNoteId());
        dto.setBookId(entity.getBookId());
        
        // 获取书籍信息
        BookEntity book = bookRepository.findByBookId(entity.getBookId()).orElse(null);
        if (book != null) {
            dto.setBookTitle(book.getTitle());
        } else {
            dto.setBookTitle("");
        }
        
        dto.setChapterId(entity.getChapterId());
        
        // 获取章节信息
        if (entity.getChapterId() != null) {
            chapterRepository.findById(entity.getChapterId())
                    .ifPresent(chapter -> dto.setChapterName(chapter.getTitle()));
        }
        if (dto.getChapterName() == null) {
            dto.setChapterName("");
        }
        
        dto.setQuote(entity.getContent()); // content字段存储quote
        dto.setStartIndex(0); // 数据库中没有此字段，使用默认值0
        dto.setEndIndex(entity.getContent() != null ? entity.getContent().length() : 0);
        dto.setPageNumber(entity.getPage() != null ? entity.getPage() : 0);
        
        // lineTypes从color字段解析（可能是单个值或逗号分隔的多个值）
        List<String> lineTypes = new ArrayList<>();
        if (entity.getColor() != null && !entity.getColor().isEmpty()) {
            if (entity.getColor().contains(",")) {
                lineTypes = Arrays.asList(entity.getColor().split(","));
            } else {
                lineTypes = Arrays.asList(entity.getColor());
            }
        } else {
            lineTypes = Arrays.asList("marker"); // 默认值
        }
        dto.setLineTypes(lineTypes);
        
        // noteContent：数据库中没有单独存储，暂时使用空字符串
        // 如果type是"thought"，可以考虑从content中提取，或者后续扩展数据库字段
        dto.setNoteContent("");
        
        dto.setNoteCreatedAt(entity.getCreatedAt());
        
        // noteType：根据type字段判断
        // "highlight" 对应 "highlight"，其他可能对应 "thought"
        if ("highlight".equals(entity.getType())) {
            dto.setNoteType("highlight");
        } else {
            dto.setNoteType("thought");
        }
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookNoteDTO> getUserRecentNotes6(Integer userId) {
        // 查询最新的6条笔记
        PageRequest pageable = PageRequest.of(0, 6);
        List<NoteEntity> notes = noteRepository.findTopNByUserIdOrderByCreatedAtDesc(userId, pageable);

        // 转换为BookNoteDTO
        return notes.stream()
                .map(this::convertToBookNoteDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将NoteEntity转换为BookNoteDTO
     */
    private BookNoteDTO convertToBookNoteDTO(NoteEntity entity) {
        BookNoteDTO dto = new BookNoteDTO();
        
        // 获取书籍标题
        BookEntity book = bookRepository.findByBookId(entity.getBookId()).orElse(null);
        if (book != null) {
            dto.setBookTitle(book.getTitle());
        } else {
            dto.setBookTitle("");
        }
        
        // quote：从content字段获取
        dto.setQuote(entity.getContent() != null ? entity.getContent() : "");
        
        // noteContent：目前数据库中没有单独存储想法内容，暂时使用空字符串
        // 如果type是"thought"，可以考虑将content作为noteContent，但根据现有逻辑，content存储的是quote
        dto.setNoteContent("");
        
        // noteDate：从createdAt格式化为字符串（格式：YYYY-MM-DD）
        if (entity.getCreatedAt() != null) {
            dto.setNoteDate(entity.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            dto.setNoteDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        // chapterId：从entity获取章节ID
        dto.setChapterId(entity.getChapterId());
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HighlightVO> getUserRecentHighlights3(Integer userId) {
        // 查询最新的3条划线（type="highlight"且color为marker、wavy或underline）
        PageRequest pageable = PageRequest.of(0, 3);
        List<NoteEntity> notes = noteRepository.findTopNByUserIdOrderByCreatedAtDesc(userId, pageable);

        // 过滤出划线类型（type="highlight"且color为marker、wavy或underline）
        // 转换为HighlightVO
        return notes.stream()
                .filter(note -> "highlight".equals(note.getType()))
                .filter(note -> {
                    String color = note.getColor();
                    return color != null && (color.equals("marker") || color.equals("wavy") || color.equals("underline"));
                })
                .limit(3) // 确保只返回3条
                .map(this::convertToHighlightVO)
                .collect(Collectors.toList());
    }

    /**
     * 将NoteEntity转换为HighlightVO
     */
    private HighlightVO convertToHighlightVO(NoteEntity entity) {
        HighlightVO vo = new HighlightVO();
        
        // 获取书籍标题
        BookEntity book = bookRepository.findByBookId(entity.getBookId()).orElse(null);
        if (book != null) {
            vo.setBookTitle(book.getTitle());
        } else {
            vo.setBookTitle("");
        }
        
        // 划线内容：从content字段获取
        vo.setContent(entity.getContent() != null ? entity.getContent() : "");
        
        // 获取章节名称
        if (entity.getChapterId() != null) {
            chapterRepository.findById(entity.getChapterId())
                    .ifPresent(chapter -> vo.setChapterName(chapter.getTitle()));
        }
        if (vo.getChapterName() == null) {
            vo.setChapterName("");
        }
        
        // 划线日期：从createdAt转换为LocalDate
        if (entity.getCreatedAt() != null) {
            vo.setHighlightDate(entity.getCreatedAt().toLocalDate());
        } else {
            vo.setHighlightDate(LocalDate.now());
        }
        
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getUserNoteCount(Integer userId) {
        Integer count = noteRepository.countByUserId(userId);
        return (count != null && count > 0) ? count.intValue() : 0;
    }
}