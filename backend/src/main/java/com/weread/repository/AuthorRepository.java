package com.weread.repository;

import com.weread.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Author Data Access Interface, for the 'author_info' table.
 */
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {

    /**
     * Finds Author information by ID.
     * Inherited from JpaRepository's findById().
     * * @param authorId Author ID
     * @return Author Entity wrapped in Optional
     */
    Optional<AuthorEntity> findByAuthorId(Integer authorId);

    /**
     * Finds Author information by name (supports uniqueness check).
     * * @param name Author name (should be unique in the database)
     * @return Author Entity
     */
    Optional<AuthorEntity> findByAuthorName(String authorName);

    /**
     * Checks if an Author name already exists (used for uniqueness validation during creation/modification).
     * * @param name Author name
     * @return true if exists, false otherwise
     */
    boolean existsByAuthorName(String authorName);
}