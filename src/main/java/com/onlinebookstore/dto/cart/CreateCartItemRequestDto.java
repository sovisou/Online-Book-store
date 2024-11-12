package com.onlinebookstore.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @Positive
    private Long bookId;
    @Min(1)
    private int quantity;
}
