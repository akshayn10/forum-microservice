package com.akshayan.commentservice.controller;

import com.akshayan.commentservice.dto.GetVoteByPostUserDto;
import com.akshayan.commentservice.dto.VoteDto;
import com.akshayan.commentservice.model.Vote;
import com.akshayan.commentservice.service.VoteService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Void> vote(@RequestBody VoteDto voteDto, @RequestHeader("Authorization") String token){
        voteService.vote(voteDto,token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/getVote")
    public ResponseEntity<Optional<Vote>> getTopByPostIdAndUserId (@RequestBody GetVoteByPostUserDto dto){
        return ResponseEntity.status(HttpStatus.OK).body(voteService.getTopByPostIdAndUserId(dto.getPostId(), dto.getUserId()));
    }
}