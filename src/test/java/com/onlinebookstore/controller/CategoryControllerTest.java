package com.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CreateCategoryRequestDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static final Long CATEGORY_ID = 1L;
    private static final String DESCRIPTION = "Books content is very dramatically.";
    private static final String SECOND_CATEGORY_NAME = "Melodrama";
    private static final String FIRST_CATEGORY_NAME = "Drama";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/add-three-default-categories.sql"));
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-categories.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category")
    public void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(FIRST_CATEGORY_NAME);
        requestDto.setDescription(DESCRIPTION);

        CategoryDto expected = new CategoryDto();
        expected.setName(FIRST_CATEGORY_NAME);
        expected.setId(CATEGORY_ID);
        expected.setDescription(DESCRIPTION);

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get all categories")
    void getAllCategories_ValidRequestDto_Success() throws Exception {
        CategoryDto firstCategoryDto = new CategoryDto();
        firstCategoryDto.setId(1L);
        firstCategoryDto.setName("Fiction");
        firstCategoryDto.setDescription("Books that contain content"
                + " that is invented or imagined, not factual.");
        CategoryDto secondCategoryDto = new CategoryDto();
        secondCategoryDto.setId(2L);
        secondCategoryDto.setName("Non-Fiction");
        secondCategoryDto.setDescription("Books that are based"
                + " on factual information or real events.");
        CategoryDto thirdCategoryDto = new CategoryDto();
        thirdCategoryDto.setId(3L);
        thirdCategoryDto.setName("Fantasy");
        thirdCategoryDto.setDescription("Books that contain magical"
                + " or supernatural elements, set in imaginary worlds.");

        Pageable pageable = PageRequest.of(0, 20);
        Page<CategoryDto> expected = new PageImpl<>(List.of(firstCategoryDto,
                secondCategoryDto, thirdCategoryDto),
                pageable, 3);

        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get a list of all available categories")
    public void getCategoryById_GivenDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(FIRST_CATEGORY_NAME);
        requestDto.setDescription(DESCRIPTION);

        CategoryDto expected = new CategoryDto();
        expected.setName(FIRST_CATEGORY_NAME);
        expected.setId(CATEGORY_ID);
        expected.setDescription(DESCRIPTION);

        MvcResult result = mockMvc.perform(get("/categories/{id}", CATEGORY_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category info by its identifier")
    public void updateCategory_GivenDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(SECOND_CATEGORY_NAME);
        requestDto.setDescription(DESCRIPTION);

        CategoryDto expected = new CategoryDto();
        expected.setName(SECOND_CATEGORY_NAME);
        expected.setId(CATEGORY_ID);
        expected.setDescription(DESCRIPTION);

        String json = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(put("/categories/{id}", CATEGORY_ID)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        reflectionEquals(expected, actual);
    }
}
