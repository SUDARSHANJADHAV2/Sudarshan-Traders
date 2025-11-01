package com.sudarshan.trader.service;

import com.sudarshan.trader.entity.DiscountRule;
import com.sudarshan.trader.exception.BadRequestException;
import com.sudarshan.trader.exception.ResourceNotFoundException;
import com.sudarshan.trader.repository.DiscountRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DiscountService {

    @Autowired
    private DiscountRuleRepository discountRuleRepository;

    public BigDecimal calculateDiscount(String sku, String brand, Integer totalQtyKg) {
        // Get all active discount rules
        List<DiscountRule> activeRules = discountRuleRepository.findByActiveTrue();

        BigDecimal highestDiscount = BigDecimal.ZERO;

        for (DiscountRule rule : activeRules) {
            // Filter rules: global (appliesTo=NULL), matching SKU, or matching brand
            boolean applies = false;

            if (rule.getAppliesTo() == null) {
                // Global rule
                applies = true;
            } else if (rule.getAppliesTo().equals(sku)) {
                // SKU-specific rule
                applies = true;
            } else if (rule.getAppliesTo().equals(brand)) {
                // Brand-specific rule
                applies = true;
            }

            if (applies) {
                // Check if quantity matches rule criteria
                boolean quantityMatches = totalQtyKg >= rule.getMinQtyKg() &&
                        (rule.getMaxQtyKg() == null || totalQtyKg <= rule.getMaxQtyKg());

                if (quantityMatches) {
                    // If multiple rules match, keep highest discount
                    if (rule.getDiscountPercent().compareTo(highestDiscount) > 0) {
                        highestDiscount = rule.getDiscountPercent();
                    }
                }
            }
        }

        return highestDiscount;
    }

    public List<DiscountRule> getActiveRules() {
        return discountRuleRepository.findByActiveTrue();
    }

    public DiscountRule createRule(DiscountRule rule) {
        // Validate minQtyKg > 0
        if (rule.getMinQtyKg() <= 0) {
            throw new BadRequestException("Minimum quantity must be greater than 0");
        }

        // Validate discountPercent between 0 and 100
        if (rule.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0 ||
                rule.getDiscountPercent().compareTo(new BigDecimal("100")) > 0) {
            throw new BadRequestException("Discount percent must be between 0 and 100");
        }

        // Save rule
        return discountRuleRepository.save(rule);
    }

    public DiscountRule updateRule(Long id, DiscountRule updates) {
        // Find existing rule
        DiscountRule existingRule = discountRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount rule not found"));

        // Update fields
        if (updates.getRuleName() != null) {
            existingRule.setRuleName(updates.getRuleName());
        }
        if (updates.getAppliesTo() != null) {
            existingRule.setAppliesTo(updates.getAppliesTo());
        }
        if (updates.getMinQtyKg() != null) {
            if (updates.getMinQtyKg() <= 0) {
                throw new BadRequestException("Minimum quantity must be greater than 0");
            }
            existingRule.setMinQtyKg(updates.getMinQtyKg());
        }
        if (updates.getMaxQtyKg() != null) {
            existingRule.setMaxQtyKg(updates.getMaxQtyKg());
        }
        if (updates.getDiscountPercent() != null) {
            if (updates.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0 ||
                    updates.getDiscountPercent().compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestException("Discount percent must be between 0 and 100");
            }
            existingRule.setDiscountPercent(updates.getDiscountPercent());
        }
        if (updates.getActive() != null) {
            existingRule.setActive(updates.getActive());
        }

        // Save and return
        return discountRuleRepository.save(existingRule);
    }

    public void deleteRule(Long id) {
        if (!discountRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Discount rule not found");
        }
        discountRuleRepository.deleteById(id);
    }
}
