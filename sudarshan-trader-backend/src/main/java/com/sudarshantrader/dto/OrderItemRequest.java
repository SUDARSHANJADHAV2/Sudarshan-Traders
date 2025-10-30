package com.sudarshantrader.backend.dto;

public class OrderItemRequest {
    private Long productId;
    private Integer qtyKg;

    public OrderItemRequest() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQtyKg() {
        return qtyKg;
    }

    public void setQtyKg(Integer qtyKg) {
        this.qtyKg = qtyKg;
    }
}
