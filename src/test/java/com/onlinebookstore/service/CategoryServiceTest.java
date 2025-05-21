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
    private static final Long CATEGORY_ID = 1L;
    private static final Long SECOND_CATEGORY_ID = 2L;
    private static final String DESCRIPTION = "Books content is very dramatically.";
    private static final String SECOND_CATEGORY_NAME = "Melodrama";
    private static final String FIRST_CATEGORY_NAME = "Drama";

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void save_validCreateCategoryRequestDto_returnCategoryDto() {
        Category category = new Category();
        category.setName(FIRST_CATEGORY_NAME);
        category.setDescription(DESCRIPTION);

        CategoryDto expectedCategoryDto = new CategoryDto();
        expectedCategoryDto.setId(CATEGORY_ID);
        expectedCategoryDto.setName(FIRST_CATEGORY_NAME);
        expectedCategoryDto.setDescription(DESCRIPTION);
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();

        when(categoryMapper.toEntity(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        CategoryDto result = categoryService.save(categoryRequestDto);
        assertEquals(expectedCategoryDto, result);
        verify(categoryMapper, times(1)).toEntity(categoryRequestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toEntity(categoryRequestDto);
    }

    @Test
    public void getById_validId_returnCategoryDto() {
        Category category = new Category();
        category.setName(FIRST_CATEGORY_NAME);
        category.setDescription(DESCRIPTION);

        CategoryDto expectedCategoryDto = new CategoryDto();
        expectedCategoryDto.setId(CATEGORY_ID);
        expectedCategoryDto.setName(FIRST_CATEGORY_NAME);
        expectedCategoryDto.setDescription(DESCRIPTION);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        CategoryDto result = categoryService.getById(CATEGORY_ID);
        assertEquals(expectedCategoryDto, result);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    public void getAllCategories_validPageable_returnCategoryDto() {
        Category fisrtCategory = new Category();
        fisrtCategory.setId(CATEGORY_ID);
        fisrtCategory.setName(FIRST_CATEGORY_NAME);
        fisrtCategory.setDescription(DESCRIPTION);

        Category secondCategory = new Category();
        secondCategory.setId(SECOND_CATEGORY_ID);
        secondCategory.setName(FIRST_CATEGORY_NAME);
        secondCategory.setDescription(DESCRIPTION);

        CategoryDto firstExpectedCategory = new CategoryDto();
        firstExpectedCategory.setId(CATEGORY_ID);
        firstExpectedCategory.setName(FIRST_CATEGORY_NAME);
        firstExpectedCategory.setDescription(DESCRIPTION);

        CategoryDto secondExpectedCategory = new CategoryDto();
        secondExpectedCategory.setId(SECOND_CATEGORY_ID);
        secondExpectedCategory.setName(SECOND_CATEGORY_NAME);
        secondExpectedCategory.setDescription(DESCRIPTION);
        List<Category> categories = List.of(fisrtCategory, secondCategory);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 10);

        when(categoryMapper.toDto(fisrtCategory)).thenReturn(firstExpectedCategory);
        when(categoryMapper.toDto(secondCategory)).thenReturn(secondExpectedCategory);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Page<CategoryDto> expectedCategories = new PageImpl<>(List.of(firstExpectedCategory,
                secondExpectedCategory), pageable, 10);

        Page<CategoryDto> result = categoryService.findAll(pageable);
        assertEquals(expectedCategories, result);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(fisrtCategory);
        verify(categoryMapper, times(1)).toDto(secondCategory);
    }

    @Test
    public void update_validCreateCategoryRequestDto_returnCategoryDto() {
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto();
        createCategoryRequestDto.setName(FIRST_CATEGORY_NAME);
        createCategoryRequestDto.setDescription(DESCRIPTION);

        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(FIRST_CATEGORY_NAME);
        category.setDescription(DESCRIPTION);

        CategoryDto expectedCategoryDto = new CategoryDto();
        expectedCategoryDto.setId(CATEGORY_ID);
        expectedCategoryDto.setName(FIRST_CATEGORY_NAME);
        expectedCategoryDto.setDescription(DESCRIPTION);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        CategoryDto result = categoryService.update(CATEGORY_ID, createCategoryRequestDto);
        assertEquals(expectedCategoryDto, result);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryMapper, times(1)).toDto(category);
    }
}
