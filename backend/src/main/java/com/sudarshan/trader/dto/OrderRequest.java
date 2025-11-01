package com.sudarshan.trader.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "Payment mode is required")
    @Pattern(regexp = "^(COD|ONLINE|BANK_TRANSFER)$",
             message = "Payment mode must be COD, ONLINE, or BANK_TRANSFER")
    private String paymentMode;

    private String notes;
}
