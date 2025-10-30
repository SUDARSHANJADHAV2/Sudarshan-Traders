package com.sudarshantrader.controller;

import com.sudarshantrader.model.Product;
import com.sudarshantrader.model.User;
import com.sudarshantrader.model.enums.OrderStatus;
import com.sudarshantrader.service.ProductService;
import com.sudarshantrader.service.OrderService;
import com.sudarshantrader.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(UserService userService, ProductService productService, OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/buyers/pending")
    public ResponseEntity<?> pendingBuyers() {
        List<User> all = userService.findAll(); // we'll add findAll method quickly
        return ResponseEntity.ok(all.stream().filter(u -> !u.isVerified()).toList());
    }

    @PutMapping("/buyers/{id}/verify")
    public ResponseEntity<?> verifyBuyer(@PathVariable Long id) {
        var user = userService.findById(id).orElse(null);
        if (user == null)
            return ResponseEntity.notFound().build();
        user.setVerified(true);
        userService.save(user);
        return ResponseEntity.ok(Map.of("message", "Buyer verified"));
    }

    // Product CRUD
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Product p) {
        Product saved = productService.save(p);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product p) {
        var existing = productService.findById(id).orElse(null);
        if (existing == null)
            return ResponseEntity.notFound().build();
        existing.setName(p.getName());
        existing.setBrand(p.getBrand());
        existing.setVariant(p.getVariant());
        existing.setPackaging(p.getPackaging());
        existing.setPricePerKg(p.getPricePerKg());
        existing.setStockKg(p.getStockKg());
        existing.setImageUrl(p.getImageUrl());
        productService.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    // Orders
    @GetMapping("/orders")
    public ResponseEntity<?> allOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        try {
            var o = orderService.updateStatus(id, OrderStatus.valueOf(status));
            return ResponseEntity.ok(o);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
