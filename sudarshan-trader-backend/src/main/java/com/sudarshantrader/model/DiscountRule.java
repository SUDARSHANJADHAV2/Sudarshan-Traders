package com.sudarshantrader.model;

import jakarta.persistence.*;

@Entity
@Table(name = "discount_rules")
public class DiscountRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // either sku or brand - admin may set one of them
    private String skuOrBrand;

    @Column(name = "min_qty_kg")
    private Integer minQtyKg;

    @Column(name = "discount_percent")
    private Double discountPercent;

    public DiscountRule() {
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkuOrBrand() {
        return skuOrBrand;
    }

    public void setSkuOrBrand(String skuOrBrand) {
        this.skuOrBrand = skuOrBrand;
    }

    public Integer getMinQtyKg() {
        return minQtyKg;
    }

    public void setMinQtyKg(Integer minQtyKg) {
        this.minQtyKg = minQtyKg;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }
}
