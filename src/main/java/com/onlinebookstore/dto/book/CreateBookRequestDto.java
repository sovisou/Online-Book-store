package com.onlinebookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;
    @NotBlank
    @Size(min = 1, max = 50)
    private String author;
    @NotBlank
    @Size(min = 10, max = 13)
    private String isbn;
    @NotNull
    @Min(0)
    private BigDecimal price;
    @NotBlank
    @Size(min = 20, max = 500)
    private String description;
    @NotBlank
    private String coverImage;
}
