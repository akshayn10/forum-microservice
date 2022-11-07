package com.akshayan.commentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetVoteByPostUserDto {
    private Long postId;
    private Long userId;
}
