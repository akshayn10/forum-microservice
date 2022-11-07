package com.akshayan.postservice.repository;

import com.akshayan.postservice.model.Category;
import com.akshayan.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

    List<Post> findAllByCategory(Category category);
    Optional<Post> findById(Long id);

    List<Post> findByUserId(Long userId);

}