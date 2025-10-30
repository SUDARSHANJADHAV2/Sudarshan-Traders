package com.sudarshantrader.backend.dto;

import java.util.List;

public class OrderRequest {
    private List<OrderItemRequest> items;
    private String paymentMode; // COD, BANK_TRANSFER, UPI, CREDIT_ACCOUNT

    public OrderRequest() {
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
