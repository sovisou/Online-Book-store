package com.onlinebookstore.dto.order;

import com.onlinebookstore.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateDto {
    @NotNull
    private Status status;
}
