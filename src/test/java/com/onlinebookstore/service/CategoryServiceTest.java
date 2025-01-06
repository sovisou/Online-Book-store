package com.onlinebookstore.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.onlinebookstore.mapper.CategoryMapper;
import com.onlinebookstore.model.Category;
import com.onlinebookstore.repository.category.CategoryRepository;
import com.onlinebookstore.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void save_validCreateCategoryRequestDto_returnCategoryDto() {
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto();
        Category category = new Category();
        CategoryDto expectedCategoryDto = new CategoryDto();
        when(categoryMapper.toEntity(createCategoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);
        CategoryDto result = categoryService.save(createCategoryRequestDto);
        assertEquals(expectedCategoryDto, result);
        verify(categoryMapper, times(1)).toEntity(createCategoryRequestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toEntity(createCategoryRequestDto);
    }

    @Test
    public void getById_validId_returnCategoryDto() {
        Long id = 1L;
        Category category = new Category();
        CategoryDto expectedCategoryDto = new CategoryDto();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);
        CategoryDto result = categoryService.getById(id);
        assertEquals(expectedCategoryDto, result);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    public void getAllCategories_validPageable_returnCategoryDto() {
        Category category1 = new Category();
        Category category2 = new Category();
        List<Category> categories = List.of(category1, category2);
        CategoryDto expectedCategoryDto1 = new CategoryDto();
        CategoryDto expectedCategoryDto2 = new CategoryDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 10);
        when(categoryMapper.toDto(category1)).thenReturn(expectedCategoryDto1);
        when(categoryMapper.toDto(category2)).thenReturn(expectedCategoryDto2);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Page<CategoryDto> expectedCategories = new PageImpl<>(List.of(expectedCategoryDto1,
                expectedCategoryDto2), pageable, 10);
        Page<CategoryDto> result = categoryService.findAll(pageable);
        assertEquals(expectedCategories, result);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category1);
        verify(categoryMapper, times(1)).toDto(category2);
    }

    @Test
    public void update_validCreateCategoryRequestDto_returnCategoryDto() {
        Long categoryId = 1L;
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto();
        Category category = new Category();
        CategoryDto expectedCategoryDto = new CategoryDto();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);
        CategoryDto result = categoryService.update(categoryId, createCategoryRequestDto);
        assertEquals(expectedCategoryDto, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
    }
}
