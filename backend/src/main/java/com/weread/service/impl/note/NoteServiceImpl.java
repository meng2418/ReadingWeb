package com.weread.service.impl.note;

import com.weread.entity.note.NoteEntity;
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

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
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
}