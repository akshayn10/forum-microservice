package com.akshayan.commentservice.repository;

import com.akshayan.commentservice.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v FROM Vote v WHERE v.postId = ?1 and v.userId = ?2 ORDER BY v.voteId DESC")
    Optional<Vote> findTopByPostIdAndUserIdOrderByVoteIdDesc(Long postId, Long userId);
}
