package com.akshayan.postservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostResponse {
    private Long id;
    private String postName;
    private String description;
    private String userName;
    private String categoryName;
    private Integer voteCount;
    private Integer commentCount;
    private String duration;
    private boolean upVote;
    private boolean downVote;
}
