package com.akshayan.commentservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;


import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @NotEmpty
    private String text;
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "postId", referencedColumnName = "postId")
    //Post removed
    private Long postId;
    private Instant createdDate;
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "userId", referencedColumnName = "userId")
//    private ForumUser forumUser;
    //ForumUser removed
    private Long userId;
}
