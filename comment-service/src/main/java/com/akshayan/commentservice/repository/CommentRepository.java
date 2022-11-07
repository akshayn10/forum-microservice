package com.akshayan.commentservice.repository;

import com.akshayan.commentservice.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.postId = ?1")
    List<Comment> findByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.userId = ?1")
    List<Comment> findAllByUserId(Long userId);

}
