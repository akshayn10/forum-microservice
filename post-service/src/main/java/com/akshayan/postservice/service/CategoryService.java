package com.akshayan.postservice.service;

import com.akshayan.postservice.dto.CategoryDto;
import com.akshayan.postservice.dto.CategoryRequestDto;
import com.akshayan.postservice.exception.ForumException;
import com.akshayan.postservice.model.Category;
import com.akshayan.postservice.repository.CategoryRepository;
import com.akshayan.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryService {
    private WebClient.Builder webClientBuilder;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private static final String AUTH_SERVICE_URL = "http://auth-service/api/auth/current-user";

    @Transactional
    public Category createCategory(CategoryRequestDto categoryRequestDto,String token) {
        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        category.setDescription(categoryRequestDto.getDescription());
        category.setCreatedDate(Instant.now());


        //get user details from auth-service
        Long userId= webClientBuilder.build().get().uri(AUTH_SERVICE_URL)
                .header("Authorization",token)
                .retrieve()
                .bodyToMono(Long.class).block();
        if (userId == null) {
            throw new ForumException("User not found");

        }
        category.setUserId(userId);
        categoryRepository.save(category);

        return category;

    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategory() {
       return  mapFromCategoryToCategoryDto(categoryRepository.findAll());
    }

    public CategoryDto getCategoryById(Long id) {

        Category category = categoryRepository.findById(id).orElseThrow(() -> new ForumException("Category not found"));
        CategoryDto dto = new CategoryDto();
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setId(category.getId());
        dto.setNumberOfPosts(postRepository.findAllByCategory(category).size());
        return dto;

    }
    private List<CategoryDto> mapFromCategoryToCategoryDto(List<Category> categories) {
        return categories.stream().map(c->{
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setName(c.getName());
            categoryDto.setDescription(c.getDescription());
            categoryDto.setId(c.getId());
            categoryDto.setNumberOfPosts(postRepository.findAllByCategory(c).size());
            return categoryDto;
        }).collect(Collectors.toList());

    }

}
