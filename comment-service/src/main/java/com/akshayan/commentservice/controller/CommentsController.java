package com.akshayan.commentservice.controller;
import com.akshayan.commentservice.dto.CommentsDto;
import com.akshayan.commentservice.service.CommentService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class CommentsController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto, @RequestHeader("Authorization") String token) {
        commentService.save(commentsDto, token);
        return new ResponseEntity<>(CREATED);
    }

    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId) {
        return ResponseEntity.status(OK)
                .body(commentService.getAllCommentsForPost(postId));
    }

    @GetMapping("/by-user/{userName}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String userName){
        return ResponseEntity.status(OK)
                .body(commentService.getAllCommentsForUser(userName));
    }
    @GetMapping("count/{postId}")
    public ResponseEntity<Integer> getCommentCount(@PathVariable Long postId){
        return ResponseEntity.status(OK)
                .body(commentService.getCommentCount(postId));
    }

}