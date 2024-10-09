package com.onlinebookstore.service;

import com.onlinebookstore.dto.BookDto;
import com.onlinebookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    List<BookDto> findAll();

    void deleteById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);
}
