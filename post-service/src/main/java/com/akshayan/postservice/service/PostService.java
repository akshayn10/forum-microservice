package com.akshayan.postservice.service;

import com.akshayan.postservice.dto.GetVoteByPostUserDto;
import com.akshayan.postservice.dto.PostRequest;
import com.akshayan.postservice.dto.PostResponse;
import com.akshayan.postservice.dto.UpdateVoteCountForPost;
import com.akshayan.postservice.exception.CategoryNotFoundException;
import com.akshayan.postservice.exception.ForumException;
import com.akshayan.postservice.exception.PostNotFoundException;
import com.akshayan.postservice.model.Category;
import com.akshayan.postservice.model.Post;
import com.akshayan.postservice.model.Vote;
import com.akshayan.postservice.model.VoteType;
import com.akshayan.postservice.repository.CategoryRepository;
import com.akshayan.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.akshayan.postservice.model.VoteType.DOWNVOTE;
import static com.akshayan.postservice.model.VoteType.UPVOTE;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final WebClient.Builder webClientBuilder;
    private static final String AUTH_SERVICE_URL = "http://auth-service/api/auth/";
    private static final String VOTE_SERVICE_URL = "http://comment-service/api/votes/";

    public void save(PostRequest postRequest,String token) {
        Category category = categoryRepository.findByName(postRequest.getCategoryName())
                .orElseThrow(() -> new CategoryNotFoundException(postRequest.getCategoryName()));
        Post post = new Post();
        post.setPostName(postRequest.getPostName());
        post.setDescription(postRequest.getDescription());
        post.setCategory(category);
        post.setCreatedDate(Instant.now());

        //get user details from auth-service
        Long userId= webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"current-user")
                .header("Authorization",token)
                .retrieve()
                .bodyToMono(Long.class).block();
        if (userId == null) {
            throw new ForumException("User not found");
        }
        post.setUserId(userId);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id,String token) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        PostResponse postResponse = new PostResponse();
        return getPostResponse(token, postResponse, post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String token) {

        System.out.println(postRepository.findAll());
        return  postToPostResponse(postRepository.findAll(),token);
    }
    private List<PostResponse> postToPostResponse(List<Post> posts,String token) {
        return posts.stream().map(post -> {
            PostResponse postResponse = new PostResponse();
            return getPostResponse(token, postResponse, post);
        }).collect(toList());
    }

    private PostResponse getPostResponse(String token, PostResponse postResponse, Post p) {
        postResponse.setPostName(p.getPostName());
        postResponse.setDescription(p.getDescription());
        postResponse.setCategoryName(p.getCategory().getName());
        postResponse.setId(p.getPostId());
        postResponse.setVoteCount(p.getVoteCount());
        String userName= webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"username-by-userid/"+p.getUserId())
                .retrieve()
                .bodyToMono(String.class).block();
        if (userName == null) {
            throw new ForumException("User not found");
        }
        postResponse.setUserName(userName);
        postResponse.setDuration(getDuration(p));
        postResponse.setUpVote(isPostUpVoted(p,token));
        postResponse.setDownVote(isPostDownVoted(p,token));
        postResponse.setCommentCount(commentCount(p));
        return postResponse;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByCategory(Long categoryId,String token) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId.toString()));
        List<Post> posts = postRepository.findAllByCategory(category);
        return postToPostResponse(posts,token);
    }


    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username,String token) {
        Long userId= webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"user-by-username/"+username)
                .retrieve()
                .bodyToMono(Long.class).block();
        if (userId == null) {
            throw new ForumException("User not found");
        }
        List<Post> posts = postRepository.findByUserId(userId);
        return postToPostResponse(posts,token);
    }

        private boolean checkVoteType(Post post, VoteType voteType,String token) {
        //logic for checking logged in status
            Boolean isLoggedIn = webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"isLoggedIn")
                    .header("Authorization",token)
                    .retrieve()
                    .bodyToMono(Boolean.class).block();

            if (isLoggedIn == null) {
                throw new ForumException("User not found");
            }
            Long userId= webClientBuilder.build().get().uri(AUTH_SERVICE_URL+"current-user")
                    .header("Authorization",token)
                    .retrieve()
                    .bodyToMono(Long.class).block();


        if (isLoggedIn) {
            GetVoteByPostUserDto getVoteByPostUserDto = new GetVoteByPostUserDto(post.getPostId(), userId);
            Optional<Vote> vote = Optional.ofNullable(webClientBuilder.build().post().uri(VOTE_SERVICE_URL + "getVote")
                    .header("Authorization", token)
                    .bodyValue(getVoteByPostUserDto)
                    .retrieve()
                    .bodyToMono(Vote.class).block());

            return vote.filter(v -> v.getVoteType().equals(voteType)).isPresent();
        }
        return false;
    }
    public void updatePost(UpdateVoteCountForPost updateVoteCountForPost) {
        Post post = postRepository.findById(updateVoteCountForPost.getPostId())
                .orElseThrow(() -> new PostNotFoundException(updateVoteCountForPost.getPostId().toString()));
        post.setVoteCount(updateVoteCountForPost.getVoteCount());
        postRepository.save(post);
    }

    Integer commentCount(Post post) {
        Integer count = webClientBuilder.build().get().uri("http://comment-service/api/comments/count/" + post.getPostId())
                .retrieve()
                .bodyToMono(Integer.class).block();
        return count;
    }

    String getDuration(Post post) {
        return  Date.from(post.getCreatedDate()).toString();
    }

    boolean isPostUpVoted(Post post,String token) {
        return checkVoteType(post, UPVOTE,token);
    }

    boolean isPostDownVoted(Post post,String token) {
        return checkVoteType(post, DOWNVOTE,token);
    }
}
