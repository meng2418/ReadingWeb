package com.weread.service.note;

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