package com.weread.service.note;

import com.weread.dto.note.NoteResponseDTO;
import com.weread.entity.note.NoteEntity;
import com.weread.vo.note.NoteVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteService {

    /**
     * 创建一篇新笔记或高亮
     * @param userId 笔记作者ID
     * @param noteEntity 笔记内容实体
     * @return 创建的笔记实体
     */
    NoteEntity createNote(Long userId, NoteEntity noteEntity);

    /**
     * 发布笔记（根据API规范）
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param chapterId 章节ID（可选）
     * @param quote 引用内容
     * @param lineType 划线类型（marker, wavy, underline）
     * @param thought 想法/笔记内容（可选）
     * @return 笔记响应DTO
     */
    NoteResponseDTO createNoteFromDTO(Integer userId, Integer bookId, Integer chapterId, 
                                      String quote, String lineType, String thought);

    /**
     * 更新笔记内容或公开状态（只允许作者修改）
     */
    NoteEntity updateNote(Long noteId, Long userId, String content, Boolean isPublic, String color);

    /**
     * 删除笔记（只允许作者删除）
     */
    void deleteNote(Long noteId, Long userId);

    /**
     * 分页查询用户的个人笔记列表
     */
    Page<NoteVO> getUserNotes(Long userId, Pageable pageable);

    /**
     * 获取某章节的所有公开笔记
     */
    List<NoteVO> getPublicChapterNotes(Integer chapterId);
}