package com.akshayan.postservice.controller;

import com.akshayan.postservice.dto.PostRequest;
import com.akshayan.postservice.dto.PostResponse;
import com.akshayan.postservice.dto.UpdateVoteCountForPost;
import com.akshayan.postservice.model.Post;
import com.akshayan.postservice.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("api/posts")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostRequest postRequest,@RequestHeader("Authorization") String token) {
        postService.save(postRequest,token);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(@RequestHeader("Authorization") String token) {
        return status(HttpStatus.OK).body(postService.getAllPosts(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id,@RequestHeader("Authorization") String token) {
        return status(HttpStatus.OK).body(postService.getPost(id,token));
    }

    @GetMapping("by-category/{id}")
    public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@PathVariable Long id,@RequestHeader("Authorization") String token) {
        return status(HttpStatus.OK).body(postService.getPostsByCategory(id,token));
    }

    @GetMapping("by-user/{name}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String name,@RequestHeader("Authorization") String token) {
        return status(HttpStatus.OK).body (postService.getPostsByUsername(name,token));
    }
    @PutMapping
    public ResponseEntity<Void> updatePost(@RequestBody UpdateVoteCountForPost updateVoteCountForPost) {
        postService.updatePost(updateVoteCountForPost);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

