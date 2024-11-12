package com.onlinebookstore.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @Positive
    @NotNull
    private Long bookId;
    @Positive
    private int quantity;
}
