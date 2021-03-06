package com.localnotes.repository;

import com.localnotes.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByPublicId(String id);

    Optional<Category> findByNameAndUserId(String name, String userId);

    List<Category> findAllByUserId(String userId);
}
