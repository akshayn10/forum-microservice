package com.akshayan.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVoteCountForPost {
    private Long postId;
    private Integer voteCount;
}
