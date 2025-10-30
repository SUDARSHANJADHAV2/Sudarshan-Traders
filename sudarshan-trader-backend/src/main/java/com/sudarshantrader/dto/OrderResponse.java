package com.sudarshantrader.backend.dto;

import com.sudarshantrader.backend.entity.Order;

public class OrderResponse {
    private Long id;
    private Double totalAmount;
    private Double gstAmount;
    private String status;
    private String invoiceUrl;

    public OrderResponse(Order o) {
        this.id = o.getId();
        this.totalAmount = o.getTotalAmount();
        this.gstAmount = o.getGstAmount();
        this.status = o.getStatus().name();
        this.invoiceUrl = o.getInvoiceUrl();
    }

    public Long getId() {
        return id;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Double getGstAmount() {
        return gstAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }
}
