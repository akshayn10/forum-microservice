package com.akshayan.commentservice.service;
import com.akshayan.commentservice.dto.CommentsDto;
import com.akshayan.commentservice.exception.ForumException;
import com.akshayan.commentservice.model.Comment;
import com.akshayan.commentservice.repository.CommentRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL = "";
    private static final String AUTH_SERVICE_URL = "http://auth-service/api/auth/";
    private final WebClient.Builder webClientBuilder;


    private final CommentRepository commentRepository;
//    private final MailContentBuilder mailContentBuilder;
//    private final MailService mailService;

    public void save(CommentsDto commentsDto,String token) {

        Long userId= webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"current-user")
                .header("Authorization",token)
                .retrieve()
                .bodyToMono(Long.class).block();
        Comment comment = new Comment();
        comment.setPostId(commentsDto.getPostId());
        comment.setText(commentsDto.getText());
        comment.setUserId(userId);
        comment.setCreatedDate(commentsDto.getCreatedDate());
        commentRepository.save(comment);

//        String message = mailContentBuilder.build(post.getForumUser().getUsername() + " posted a comment on your post." + POST_URL);
//        sendCommentNotification(message, post.getForumUser());

    }
    public Integer getCommentCount(Long postId) {
        return commentRepository.findByPostId(postId).size();
    }

    private void sendCommentNotification(String message, Long forumUserId) {
//        mailService.sendMail(new NotificationEmail(forumUser.getUsername() + " Commented on your post", forumUser.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        return mapCommentToCommentsDto(commentRepository.findByPostId(postId));
    }
    public List<CommentsDto> mapCommentToCommentsDto(List<Comment> comments){
        return comments.stream().map(comment -> {
            CommentsDto commentsDto = new CommentsDto();
            commentsDto.setPostId(comment.getPostId());
            commentsDto.setText(comment.getText());
            commentsDto.setCreatedDate(comment.getCreatedDate());
            commentsDto.setUserName(webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"username-by-userid/"+comment.getUserId())
                    .retrieve()
                    .bodyToMono(String.class).block());
            return commentsDto;
        }).collect(toList());
    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {
        Long userId = webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"user-by-username/"+userName)
                .retrieve()
                .bodyToMono(Long.class).block();

        return mapCommentToCommentsDto(commentRepository.findAllByUserId(userId));
    }

    public boolean containsSwearWords(String comment) {
        if (comment.contains("shit")) {
            throw new ForumException("Comments contains unacceptable language");
        }
        return false;
    }
}
