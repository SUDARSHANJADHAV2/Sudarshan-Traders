package com.sudarshan.trader.repository;

import com.sudarshan.trader.entity.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Long> {

    List<DiscountRule> findByActiveTrue();

    List<DiscountRule> findByAppliesToAndActiveTrue(String appliesTo);

    List<DiscountRule> findByAppliesTo(String appliesTo);

    List<DiscountRule> findAllByOrderByMinQtyKgAsc();
}
