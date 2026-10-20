package com.newsblog.repository;

import com.newsblog.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<Tag> findAllByOrderByNameAsc();
}
