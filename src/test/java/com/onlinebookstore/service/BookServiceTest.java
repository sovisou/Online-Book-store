package com.onlinebookstore.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.book.BookSearchParameters;
import com.onlinebookstore.dto.book.CreateBookRequestDto;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.onlinebookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final Long FIRST_BOOK_ID = 1L;
    private static final Long SECOND_BOOK_ID = 2L;
    private static final String FIRST_BOOK_TITLE = "Test Title";
    private static final String SECOND_BOOK_TITLE = "Test Title2";
    private static final String AUTHOR = "Test Author";
    private static final String FIRST_BOOK_ISBN = "123-4567890123";
    private static final String SECOND_BOOK_ISBN = "123-4567890127";
    private static final BigDecimal PRICE = new BigDecimal("29.99");
    private static final String DESCRIPTION = "Test Description";
    private static final String COVER_IMAGE = "test_cover.jpg";
    private static final Long CATEGORY_ID = 1L;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    public void save_validCreateRequestBookDto_returnBookDto() {
        Book book = new Book();
        book.setId(FIRST_BOOK_ID);
        book.setTitle(FIRST_BOOK_TITLE);
        book.setAuthor(AUTHOR);
        book.setIsbn(FIRST_BOOK_ISBN);
        book.setPrice(PRICE);
        book.setDescription(DESCRIPTION);
        book.setCoverImage(COVER_IMAGE);

        BookDto expectedBook = new BookDto();
        expectedBook.setId(FIRST_BOOK_ID);
        expectedBook.setTitle(FIRST_BOOK_TITLE);
        expectedBook.setAuthor(AUTHOR);
        expectedBook.setIsbn(FIRST_BOOK_ISBN);
        expectedBook.setPrice(PRICE);
        expectedBook.setDescription(DESCRIPTION);
        expectedBook.setCoverImage(COVER_IMAGE);
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedBook);

        BookDto result = bookService.save(requestDto);
        assertEquals(expectedBook, result);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    public void findById_validBookId_returnBookDto() {
        BookDto expectedBook = new BookDto();
        expectedBook.setId(FIRST_BOOK_ID);
        expectedBook.setTitle(FIRST_BOOK_TITLE);
        expectedBook.setAuthor(AUTHOR);
        expectedBook.setIsbn(FIRST_BOOK_ISBN);
        expectedBook.setPrice(PRICE);
        expectedBook.setDescription(DESCRIPTION);
        expectedBook.setCoverImage(COVER_IMAGE);
        Book book = new Book();

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedBook);

        BookDto result = bookService.findById(FIRST_BOOK_ID);
        assertEquals(expectedBook, result);
        verify(bookRepository, times(1)).findById(FIRST_BOOK_ID);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    public void findAll_validBookDto_returnBookDto() {
        Book firstBook = new Book();
        firstBook.setId(FIRST_BOOK_ID);
        firstBook.setTitle(FIRST_BOOK_TITLE);
        firstBook.setAuthor(AUTHOR);
        firstBook.setIsbn(FIRST_BOOK_ISBN);
        firstBook.setPrice(PRICE);
        firstBook.setDescription(DESCRIPTION);
        firstBook.setCoverImage(COVER_IMAGE);

        Book secondBook = new Book();
        secondBook.setId(SECOND_BOOK_ID);
        secondBook.setTitle(SECOND_BOOK_TITLE);
        secondBook.setAuthor(AUTHOR);
        secondBook.setIsbn(SECOND_BOOK_ISBN);
        secondBook.setPrice(PRICE);
        secondBook.setDescription(DESCRIPTION);
        secondBook.setCoverImage(COVER_IMAGE);

        BookDto firstExpectedBook = new BookDto();
        firstExpectedBook.setId(FIRST_BOOK_ID);
        firstExpectedBook.setTitle(FIRST_BOOK_TITLE);
        firstExpectedBook.setAuthor(AUTHOR);
        firstExpectedBook.setIsbn(FIRST_BOOK_ISBN);
        firstExpectedBook.setPrice(PRICE);
        firstExpectedBook.setDescription(DESCRIPTION);
        firstExpectedBook.setCoverImage(COVER_IMAGE);

        BookDto secondExpectedBook = new BookDto();
        secondExpectedBook.setId(SECOND_BOOK_ID);
        secondExpectedBook.setTitle(SECOND_BOOK_TITLE);
        secondExpectedBook.setAuthor(AUTHOR);
        secondExpectedBook.setIsbn(SECOND_BOOK_ISBN);
        secondExpectedBook.setPrice(PRICE);
        secondExpectedBook.setDescription(DESCRIPTION);
        secondExpectedBook.setCoverImage(COVER_IMAGE);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(firstBook, secondBook);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(firstBook)).thenReturn(firstExpectedBook);
        when(bookMapper.toDto(secondBook)).thenReturn(secondExpectedBook);

        List<BookDto> result = bookService.findAll(pageable);
        List<BookDto> expectedBooks = List.of(firstExpectedBook, secondExpectedBook);
        assertEquals(expectedBooks, result);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(firstBook);
        verify(bookMapper, times(1)).toDto(secondBook);
    }

    @Test
    public void update_validUpdateBookDto_returnUpdatedBookDto() {
        CreateBookRequestDto updateBookDto = new CreateBookRequestDto();
        updateBookDto.setTitle(FIRST_BOOK_TITLE);
        updateBookDto.setAuthor(AUTHOR);
        updateBookDto.setIsbn(FIRST_BOOK_ISBN);
        updateBookDto.setPrice(PRICE);
        updateBookDto.setDescription(DESCRIPTION);
        updateBookDto.setCoverImage(COVER_IMAGE);
        updateBookDto.setCategoryIds(Collections.emptySet());

        Book book = new Book();
        book.setTitle(SECOND_BOOK_TITLE);
        book.setAuthor(AUTHOR);
        book.setIsbn(SECOND_BOOK_ISBN);
        book.setPrice(PRICE);
        book.setDescription(DESCRIPTION);
        book.setCoverImage(COVER_IMAGE);

        BookDto expectedBook = new BookDto();
        expectedBook.setTitle(FIRST_BOOK_TITLE);
        expectedBook.setAuthor(AUTHOR);
        expectedBook.setIsbn(FIRST_BOOK_ISBN);
        expectedBook.setPrice(PRICE);
        expectedBook.setDescription(DESCRIPTION);
        expectedBook.setCoverImage(COVER_IMAGE);

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).updateBookFromDto(updateBookDto, book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedBook);

        BookDto result = bookService.updateById(FIRST_BOOK_ID, updateBookDto);
        assertEquals(expectedBook, result);
        verify(bookRepository, times(1)).findById(FIRST_BOOK_ID);
        verify(bookMapper, times(1)).updateBookFromDto(updateBookDto, book);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    public void search_validSearchParams_returnBookDto() {
        String[] titles = {};
        String[] authors = {};
        BookSearchParameters parameters = new BookSearchParameters(titles, authors);
        Specification<Book> specification = mock(Specification.class);
        List<Book> books = List.of(new Book(), new Book());
        List<BookDto> expectedBooks = List.of(new BookDto(), new BookDto());

        when(bookSpecificationBuilder.build(parameters)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedBooks.get(0),
                expectedBooks.get(1));

        List<BookDto> result = bookService.search(parameters);
        assertEquals(expectedBooks, result);
        verify(bookSpecificationBuilder, times(1)).build(parameters);
        verify(bookRepository, times(1)).findAll(specification);
        verify(bookMapper, times(books.size())).toDto(any(Book.class));
    }

    @Test
    public void getBooksByCategoryId_validCategoryId_returnListOfBookDto() {
        Long categoryId = CATEGORY_ID;
        List<Book> books = List.of(new Book(), new Book());
        List<BookDtoWithoutCategoryIds> expectedBooks = List.of(new BookDtoWithoutCategoryIds(),
                new BookDtoWithoutCategoryIds());

        when(bookRepository.findAllByCategories_Id(categoryId)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategories(any(Book.class)))

                .thenReturn(expectedBooks.get(0), expectedBooks.get(1));
        List<BookDtoWithoutCategoryIds> result = bookService.getBooksByCategoryId(categoryId);
        assertEquals(expectedBooks, result);
        verify(bookRepository, times(1)).findAllByCategories_Id(categoryId);
        verify(bookMapper, times(books.size())).toDtoWithoutCategories(any(Book.class));
    }
}
