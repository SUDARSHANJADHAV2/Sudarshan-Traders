package com.sudarshan.trader.controller;

import com.sudarshan.trader.entity.DiscountRule;
import com.sudarshan.trader.entity.Product;
import com.sudarshan.trader.entity.User;
import com.sudarshan.trader.service.AdminService;
import com.sudarshan.trader.service.DiscountService;
import com.sudarshan.trader.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DiscountService discountService;

    // Buyer Management
    @GetMapping("/buyers/pending")
    public ResponseEntity<List<User>> getPendingBuyers() {
        List<User> pendingBuyers = adminService.getPendingBuyers();
        return ResponseEntity.ok(pendingBuyers);
    }

    @PutMapping("/buyers/{id}/verify")
    public ResponseEntity<Map<String, Object>> verifyBuyer(@PathVariable Long id) {
        adminService.verifyBuyer(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Buyer verified successfully");
        response.put("buyerId", id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/buyers/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectBuyer(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        adminService.rejectBuyer(id, reason);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Buyer registration rejected");

        return ResponseEntity.ok(response);
    }

    // Product Management
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = adminService.getAllProductsAdmin();
        return ResponseEntity.ok(products);
    }

    // Order Management
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Map<String, String>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        orderService.updateOrderStatus(id, status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Order status updated successfully");
        response.put("newStatus", status);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{id}/confirm")
    public ResponseEntity<Map<String, String>> confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Order confirmed and stock decremented");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/export")
    public ResponseEntity<Map<String, String>> exportOrders() {
        String csvPath = adminService.exportOrdersCSV();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Orders exported successfully");
        response.put("filePath", csvPath);

        return ResponseEntity.ok(response);
    }

    // Discount Rules Management
    @GetMapping("/discounts")
    public ResponseEntity<List<DiscountRule>> getAllDiscountRules() {
        List<DiscountRule> rules = discountService.getActiveRules();
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/discounts")
    public ResponseEntity<DiscountRule> createDiscountRule(@Valid @RequestBody DiscountRule rule) {
        DiscountRule createdRule = discountService.createRule(rule);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

    @PutMapping("/discounts/{id}")
    public ResponseEntity<DiscountRule> updateDiscountRule(
            @PathVariable Long id,
            @RequestBody DiscountRule rule) {

        DiscountRule updatedRule = discountService.updateRule(id, rule);
        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<Void> deleteDiscountRule(@PathVariable Long id) {
        discountService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
