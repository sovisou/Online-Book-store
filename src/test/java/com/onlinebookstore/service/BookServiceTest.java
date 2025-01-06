package com.onlinebookstore.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.book.BookSearchParameters;
import com.onlinebookstore.dto.book.CreateBookRequestDto;
import com.onlinebookstore.dto.book.UpdateBookDto;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.onlinebookstore.service.impl.BookServiceImpl;
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
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book book = new Book();
        BookDto expectedBook = new BookDto();
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
        Book book = new Book();
        Long bookId = 1L;
        BookDto expectedBook = new BookDto();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedBook);
        BookDto result = bookService.findById(bookId);
        assertEquals(expectedBook, result);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    public void findAll_validBookDto_returnBookDto() {
        Book book1 = new Book();
        Book book2 = new Book();
        List<Book> books = List.of(book1, book2);
        BookDto bookDto1 = new BookDto();
        BookDto bookDto2 = new BookDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        List<BookDto> expectedBooks = List.of(bookDto1, bookDto2);
        List<BookDto> result = bookService.findAll(pageable);
        assertEquals(expectedBooks, result);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book1);
        verify(bookMapper, times(1)).toDto(book2);
    }

    @Test
    public void update_validUpdateBookDto_returnUpdatedBookDto() {
        Long bookId = 1L;
        UpdateBookDto updateBookDto = new UpdateBookDto();
        Book book = new Book();
        BookDto expectedBook = new BookDto();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedBook);
        BookDto result = bookService.updateById(bookId, updateBookDto);
        assertEquals(expectedBook, result);
        verify(bookRepository, times(1)).findById(bookId);
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
        Long categoryId = 1L;
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
