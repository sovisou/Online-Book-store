package com.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.book.CreateBookRequestDto;
import com.onlinebookstore.dto.book.UpdateBookDto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
public class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(67.00);
    private static final String BOOK_AUTHOR_NAME = "Author";
    private static final String BOOK_COVER_IMAGE = "Cover Image";
    private static final String BOOK_DESCRIPTION = "Book Description";
    private static final String FIRST_BOOK_ISBN = "4354657687";
    private static final String FIRST_BOOK_TITLE = "First Book";
    private static final String SECOND_BOOK_ISBN = "43546576879";

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
                    new ClassPathResource("database/books/add-three-default-books.sql"));
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
                    new ClassPathResource("database/books/delete-books.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(FIRST_BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR_NAME);
        requestDto.setIsbn(FIRST_BOOK_ISBN);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setDescription(BOOK_DESCRIPTION);
        requestDto.setCoverImage(BOOK_COVER_IMAGE);

        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setTitle(requestDto.getTitle());
        expectedBookDto.setAuthor(requestDto.getAuthor());
        expectedBookDto.setIsbn(requestDto.getIsbn());
        expectedBookDto.setPrice(requestDto.getPrice());
        expectedBookDto.setDescription(requestDto.getDescription());
        expectedBookDto.setCoverImage(requestDto.getCoverImage());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actualBookDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertNotNull(actualBookDto);
        reflectionEquals(expectedBookDto, actualBookDto);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get all books")
    void getAllBooks_ValidRequestDto_Success() throws Exception {
        BookDto firstBookDto = new BookDto();
        firstBookDto.setId(1L);
        firstBookDto.setTitle("First Book");
        firstBookDto.setAuthor("Author One");
        firstBookDto.setIsbn("9781234567890");
        firstBookDto.setPrice(BigDecimal.valueOf(25.99));
        firstBookDto.setDescription("This is the first book description");
        firstBookDto.setCoverImage("first_book_cover.jpg");
        firstBookDto.setCategories(new HashSet<>());
        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(2L);
        secondBookDto.setTitle("Second Book");
        secondBookDto.setAuthor("Author Two");
        secondBookDto.setIsbn("9781234567891");
        secondBookDto.setPrice(BigDecimal.valueOf(15.49));
        secondBookDto.setDescription("This is the second book description");
        secondBookDto.setCoverImage("second_book_cover.jpg");
        secondBookDto.setCategories(new HashSet<>());
        BookDto thirdBookDto = new BookDto();
        thirdBookDto.setId(3L);
        thirdBookDto.setTitle("Third Book");
        thirdBookDto.setAuthor("Author Three");
        thirdBookDto.setIsbn("9781234567892");
        thirdBookDto.setPrice(BigDecimal.valueOf(18.99));
        thirdBookDto.setDescription("This is the third book description");
        thirdBookDto.setCoverImage("third_book_cover.jpg");
        thirdBookDto.setCategories(new HashSet<>());
        List<BookDto> expected = new ArrayList<>();

        expected.add(firstBookDto);
        expected.add(secondBookDto);
        expected.add(thirdBookDto);

        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actualBookDto = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertEquals(3, actualBookDto.length);
        assertEquals(expected, Arrays.stream(actualBookDto).toList());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get book by id")
    void getBookById_ValidRequestDto_Success() throws Exception {
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(FIRST_BOOK_TITLE);
        expected.setAuthor(BOOK_AUTHOR_NAME);
        expected.setIsbn(FIRST_BOOK_ISBN);
        expected.setPrice(BOOK_PRICE);
        expected.setDescription(BOOK_DESCRIPTION);
        expected.setCoverImage(BOOK_COVER_IMAGE);

        MvcResult result = mockMvc.perform(get("/books/{id}", expected.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actualBookDto = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto.class);
        reflectionEquals(expected, actualBookDto);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Search books by id")
    void searchBooks_ValidRequest_Success() throws Exception {
        BookDto requestDto = new BookDto();
        requestDto.setId(1L);
        requestDto.setTitle(FIRST_BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR_NAME);
        requestDto.setIsbn(FIRST_BOOK_ISBN);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setDescription(BOOK_DESCRIPTION);
        requestDto.setCoverImage(BOOK_COVER_IMAGE);
        requestDto.setCategories(new HashSet<>());
        Pageable pageable = PageRequest.of(0, 20);

        Page<BookDto> expected = new PageImpl<>(List.of(requestDto), pageable, 1);

        MvcResult result = mockMvc.perform(get("/books/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actual = result.getResponse().getContentAsString();

        reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Update book info by its identifier")
    void updateBook_ValidRequest_Success() throws Exception {
        UpdateBookDto requestDto = new UpdateBookDto();
        requestDto.setTitle(FIRST_BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR_NAME);
        requestDto.setIsbn(SECOND_BOOK_ISBN);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setDescription(BOOK_DESCRIPTION);
        requestDto.setCoverImage(BOOK_COVER_IMAGE);
        requestDto.setCategories(new HashSet<>());

        BookDto expected = new BookDto();
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setDescription(requestDto.getDescription());
        expected.setCoverImage(requestDto.getCoverImage());
        expected.setCategories(requestDto.getCategories());
        Long bookId = 1L;

        String json = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(put("/books/{id}", bookId)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get a list of books by its category identifier")
    public void getBooksByCategoryId_GivenDto_Success() throws Exception {
        Long categoryId = 1L;
        BookDtoWithoutCategoryIds requestDto = new BookDtoWithoutCategoryIds();
        requestDto.setId(1L);
        requestDto.setTitle(FIRST_BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR_NAME);
        requestDto.setIsbn(FIRST_BOOK_ISBN);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setDescription(BOOK_DESCRIPTION);
        requestDto.setCoverImage(BOOK_COVER_IMAGE);
        Pageable pageable = PageRequest.of(0, 20);

        Page<BookDtoWithoutCategoryIds> expected = new PageImpl<>(List.of(requestDto), pageable, 1);

        MvcResult result = mockMvc.perform(get("/books/{id}/books", categoryId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actual = result.getResponse().getContentAsString();

        reflectionEquals(expected, actual);
    }
}
