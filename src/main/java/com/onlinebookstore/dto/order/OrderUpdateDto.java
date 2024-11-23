package com.onlinebookstore.dto.order;

import com.onlinebookstore.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderUpdateDto {
    @NotBlank
    private Status status;
}
