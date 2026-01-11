package com.weread.repository.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.weread.entity.author.AuthorEntity;

import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.query.Param;

/**
 * Author Data Access Interface, for the 'author_info' table.
 */
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {

    /**
     * Finds Author information by ID.
     * Inherited from JpaRepository's findById().
     * * @param authorId Author ID
     * 
     * @return Author Entity wrapped in Optional
     */
    Optional<AuthorEntity> findByAuthorId(Integer authorId);

    /**
     * Finds Author information by name (supports uniqueness check).
     * * @param name Author name (should be unique in the database)
     * 
     * @return Author Entity
     */
    Optional<AuthorEntity> findByAuthorName(String authorName);

    /**
     * Checks if an Author name already exists (used for uniqueness validation
     * during creation/modification).
     * * @param name Author name
     * 
     * @return true if exists, false otherwise
     */
    boolean existsByAuthorName(String authorName);

    // 根据姓名模糊搜索（忽略大小写）
    List<AuthorEntity> findByAuthorNameContainingIgnoreCase(String authorName);

    // 根据简介搜索
    @Query("SELECT a FROM AuthorEntity a WHERE a.bio LIKE %:keyword%")
    List<AuthorEntity> findByBioContaining(@Param("keyword") String keyword);
}