package com.akshayan.postservice.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long postId;
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;
    @Nullable
    @Lob
    private String description;
    private Integer voteCount = 0;
    private Long userId;
    private Instant createdDate;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    private Category category;
}