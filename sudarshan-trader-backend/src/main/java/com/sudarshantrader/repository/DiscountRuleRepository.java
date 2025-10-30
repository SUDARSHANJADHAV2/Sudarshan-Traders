package com.sudarshantrader.repository;

import com.sudarshantrader.model.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Long> {
    List<DiscountRule> findBySkuOrBrand(String skuOrBrand);
}
