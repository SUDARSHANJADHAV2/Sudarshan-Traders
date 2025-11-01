package com.sudarshan.trader.service;

import com.sudarshan.trader.entity.Product;
import com.sudarshan.trader.exception.BadRequestException;
import com.sudarshan.trader.exception.DuplicateResourceException;
import com.sudarshan.trader.exception.InsufficientStockException;
import com.sudarshan.trader.exception.ResourceNotFoundException;
import com.sudarshan.trader.repository.OrderItemRepository;
import com.sudarshan.trader.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Product> getAllProducts(String brand, String variant, String packaging, String search) {
        // If search keyword provided
        if (search != null && !search.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(search);
        }

        // Apply filter combinations
        if (brand != null && variant != null && packaging != null) {
            return productRepository.findByBrandAndVariantAndPackaging(brand, variant, packaging);
        } else if (brand != null && variant != null) {
            return productRepository.findByBrandAndVariant(brand, variant);
        } else if (brand != null && packaging != null) {
            return productRepository.findByBrandAndPackaging(brand, packaging);
        } else if (variant != null && packaging != null) {
            return productRepository.findByVariantAndPackaging(variant, packaging);
        } else if (brand != null) {
            return productRepository.findByBrand(brand);
        } else if (variant != null) {
            return productRepository.findByVariant(variant);
        } else if (packaging != null) {
            return productRepository.findByPackaging(packaging);
        }

        // Return all products if no filters
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product createProduct(Product product) {
        // Validate SKU unique
        if (productRepository.existsBySku(product.getSku())) {
            throw new DuplicateResourceException("SKU already in use");
        }

        // Validate pricePerKg > 0
        if (product.getPricePerKg().doubleValue() <= 0) {
            throw new BadRequestException("Price per kg must be greater than 0");
        }

        // Validate stockKg >= 0
        if (product.getStockKg() < 0) {
            throw new BadRequestException("Stock cannot be negative");
        }

        // Save product
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updates) {
        // Find existing product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Update fields (cannot change id or sku)
        if (updates.getName() != null) {
            existingProduct.setName(updates.getName());
        }
        if (updates.getBrand() != null) {
            existingProduct.setBrand(updates.getBrand());
        }
        if (updates.getVariant() != null) {
            existingProduct.setVariant(updates.getVariant());
        }
        if (updates.getPackaging() != null) {
            existingProduct.setPackaging(updates.getPackaging());
        }
        if (updates.getPricePerKg() != null) {
            if (updates.getPricePerKg().doubleValue() <= 0) {
                throw new BadRequestException("Price per kg must be greater than 0");
            }
            existingProduct.setPricePerKg(updates.getPricePerKg());
        }
        if (updates.getStockKg() != null) {
            if (updates.getStockKg() < 0) {
                throw new BadRequestException("Stock cannot be negative");
            }
            existingProduct.setStockKg(updates.getStockKg());
        }
        if (updates.getImageUrl() != null) {
            existingProduct.setImageUrl(updates.getImageUrl());
        }
        if (updates.getDescription() != null) {
            existingProduct.setDescription(updates.getDescription());
        }

        // Save updated product
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        // Check if product exists
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        // Check if product has existing orders
        if (orderItemRepository.existsByProductId(id)) {
            throw new BadRequestException("Cannot delete product with existing orders");
        }

        // Delete product
        productRepository.deleteById(id);
    }

    public void adjustStock(Long productId, Integer kgAdjustment) {
        // Find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Calculate new stock
        int newStock = product.getStockKg() + kgAdjustment;

        // Ensure stockKg doesn't go below 0
        if (newStock < 0) {
            throw new InsufficientStockException("Insufficient stock. Cannot reduce below 0.");
        }

        // Update stock
        product.setStockKg(newStock);
        productRepository.save(product);
    }
}
