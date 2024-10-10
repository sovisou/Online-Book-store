package com.onlinebookstore.repository.book.spec;

import com.onlinebookstore.dto.BookSearchParameters;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationBuilder;
import com.onlinebookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> specification = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider("title")
                            .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider("author")
                            .getSpecification(searchParameters.authors()));
        }
        return specification;
    }
}
