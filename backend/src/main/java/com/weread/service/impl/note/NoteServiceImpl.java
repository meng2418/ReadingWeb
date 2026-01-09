package com.weread.service.impl.note;

import com.weread.dto.note.NoteResponseDTO;
import com.weread.entity.BookEntity;
import com.weread.entity.note.NoteEntity;
import com.weread.repository.BookRepository;
import com.weread.repository.note.NoteRepository;
import com.weread.service.note.NoteService;
import com.weread.vo.note.NoteVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final BookRepository bookRepository;

    public NoteServiceImpl(NoteRepository noteRepository, BookRepository bookRepository) {
        this.noteRepository = noteRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public NoteEntity createNote(Long userId, NoteEntity newNote) {
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
    public NoteEntity updateNote(Long noteId, Long userId, String content, Boolean isPublic, String color) {
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
    public void deleteNote(Long noteId, Long userId) {
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
    public Page<NoteVO> getUserNotes(Long userId, Pageable pageable) {
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
        note.setUserId(userId.longValue());
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
}