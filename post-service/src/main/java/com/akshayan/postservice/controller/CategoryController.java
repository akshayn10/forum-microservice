package com.akshayan.postservice.controller;

import com.akshayan.postservice.dto.CategoryDto;
import com.akshayan.postservice.dto.CategoryRequestDto;
import com.akshayan.postservice.model.Category;
import com.akshayan.postservice.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequestDto categoryRequestDto, @RequestHeader("Authorization") String token){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.createCategory(categoryRequestDto,token));
    }
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategory(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategory());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(id));
    }
}
