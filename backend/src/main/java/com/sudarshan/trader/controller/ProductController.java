package com.sudarshan.trader.controller;

import com.sudarshan.trader.entity.Product;
import com.sudarshan.trader.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String variant,
            @RequestParam(required = false) String packaging,
            @RequestParam(required = false) String search) {

        List<Product> products = productService.getAllProducts(brand, variant, packaging, search);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Invalid file type. Only images allowed.");
        }

        // Validate file size (5MB max)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        try {
            // Create uploads directory if not exists
            Path uploadsDir = Paths.get("uploads/products");
            Files.createDirectories(uploadsDir);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String filename = "product-" + System.currentTimeMillis() + "-" + originalFilename;
            Path filePath = uploadsDir.resolve(filename);

            // Save file
            file.transferTo(filePath.toFile());

            // Return relative URL
            String imageUrl = "/uploads/products/" + filename;
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/adjust-stock")
    public ResponseEntity<Void> adjustStock(@PathVariable Long id, @RequestParam Integer adjustment) {
        productService.adjustStock(id, adjustment);
        return ResponseEntity.ok().build();
    }
}
