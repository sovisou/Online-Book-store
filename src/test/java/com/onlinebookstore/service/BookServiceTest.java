package com.onlinebookstore.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.CreateBookRequestDto;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.onlinebookstore.service.impl.BookServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
