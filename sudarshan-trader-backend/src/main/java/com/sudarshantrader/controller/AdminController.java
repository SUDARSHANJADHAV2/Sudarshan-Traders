package com.sudarshantrader.controller;

import com.sudarshantrader.entity.Product;
import com.sudarshantrader.entity.Order;
import com.sudarshantrader.entity.OrderStatus;
import com.sudarshantrader.service.ProductService;
import com.sudarshantrader.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // ✅ Create new product
    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    // ✅ Update product
    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productService.save(product);
    }

    // ✅ Delete product
    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
    }

    // ✅ View all orders
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    // ✅ Update order status
    @PutMapping("/orders/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
}
