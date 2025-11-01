package com.sudarshan.trader.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private Integer qtyKg;
    private BigDecimal pricePerKg;
    private BigDecimal lineTotal;
}
