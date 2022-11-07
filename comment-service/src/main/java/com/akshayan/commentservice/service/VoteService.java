package com.akshayan.commentservice.service;

import com.akshayan.commentservice.dto.Post;
import com.akshayan.commentservice.dto.PostResponse;
import com.akshayan.commentservice.dto.UpdateVoteCountForPost;
import com.akshayan.commentservice.dto.VoteDto;
import com.akshayan.commentservice.exception.ForumException;
import com.akshayan.commentservice.model.Vote;
import com.akshayan.commentservice.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

import static com.akshayan.commentservice.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private static final String POST_SERVICE_URL = "http://post-service/api/posts/";
    private static final String AUTH_SERVICE_URL = "http://auth-service/api/auth/";
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public void vote(VoteDto voteDto,String token) {
    //get post from post service
        PostResponse post = webClientBuilder.build().get().uri(POST_SERVICE_URL  +voteDto.getPostId())
                .header("Authorization",token)
                .retrieve()
                .bodyToMono(PostResponse.class).block();

        if(post == null ){
            throw new ForumException("Post Not Found with ID - " + voteDto.getPostId());
        }
        Long userId= webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"current-user")
                .header("Authorization",token)
                .retrieve()
                .bodyToMono(Long.class).block();

        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostIdAndUserIdOrderByVoteIdDesc(voteDto.getPostId(), userId);

        if (voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType()
                        .equals(voteDto.getVoteType())) {
            throw new ForumException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }
        if(post.getVoteCount() == null){
            post.setVoteCount(0);
        }

        if (UPVOTE.equals(voteDto.getVoteType())) {

            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteByPostAndUser.ifPresent(voteRepository::delete);
        Vote vote = new Vote();
        vote.setVoteType(voteDto.getVoteType());
        vote.setPostId(voteDto.getPostId());
        vote.setUserId(userId);
        voteRepository.save(vote);

        UpdateVoteCountForPost updateVoteCountForPost = new UpdateVoteCountForPost();
        updateVoteCountForPost.setVoteCount(post.getVoteCount());
        updateVoteCountForPost.setPostId(post.getId());


        // update post service
        webClientBuilder.build().put().uri(POST_SERVICE_URL).bodyValue(updateVoteCountForPost).retrieve().bodyToMono(Post.class).block();
        return;
    }

    public Optional<Vote> getTopByPostIdAndUserId(Long postId, Long userId) {
        return voteRepository.findTopByPostIdAndUserIdOrderByVoteIdDesc(postId, userId);
    }
}