package com.sudarshan.trader.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private Long buyerId;
    private String buyerCompanyName;
    private String buyerEmail;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal gstAmount;
    private BigDecimal finalAmount;
    private String status;
    private String paymentMode;
    private String paymentStatus;
    private String razorpayOrderId;
    private String razorpayKey;
    private String invoiceUrl;
    private String notes;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
