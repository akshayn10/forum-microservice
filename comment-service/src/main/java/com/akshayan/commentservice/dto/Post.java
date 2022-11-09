package com.akshayan.commentservice.dto;
import lombok.*;
import org.springframework.lang.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long postId;
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;
    @Nullable
    private String description;
    private Integer voteCount = 0;
    private Long userId;
    private Instant createdDate;
}