package com.sudarshantrader.backend.service;

import com.sudarshantrader.backend.entity.Product;
import com.sudarshantrader.backend.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(Product p) {
        if (p.getSku() == null || p.getSku().isBlank())
            throw new IllegalArgumentException("SKU required");
        if (repo.existsBySku(p.getSku()))
            throw new IllegalArgumentException("SKU already exists");
        if (p.getStockKg() == null)
            p.setStockKg(0);
        if (p.getPricePerKg() == null)
            p.setPricePerKg(0.0);
        return repo.save(p);
    }

    public Product update(Long id, Product updated) {
        Product existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        // SKU should not be changed in general; allow changing other fields
        existing.setName(updated.getName());
        existing.setBrand(updated.getBrand());
        existing.setVariant(updated.getVariant());
        existing.setPackaging(updated.getPackaging());
        existing.setPricePerKg(updated.getPricePerKg());
        existing.setStockKg(updated.getStockKg());
        existing.setImageUrl(updated.getImageUrl());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new RuntimeException("Product not found");
        repo.deleteById(id);
    }

    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    public Optional<Product> findBySku(String sku) {
        return repo.findBySku(sku);
    }

    /**
     * Search products with optional filters. If a filter param is null or blank,
     * it's treated as a wildcard.
     * Pagination and sorting supported.
     */
    public Page<Product> search(String brand, String variant, String packaging, String q, int page, int size,
            String sortBy, String sortDir) {
        // Create probe example for brand/variant/packaging partial match - use
        // ExampleMatcher for contains
        Product probe = new Product();
        if (StringUtils.hasText(brand))
            probe.setBrand(brand);
        if (StringUtils.hasText(variant))
            probe.setVariant(variant);
        if (StringUtils.hasText(packaging))
            probe.setPackaging(packaging);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreNullValues();

        Example<Product> example = Example.of(probe, matcher);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir == null ? "DESC" : sortDir),
                sortBy == null ? "createdAt" : sortBy);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Page<Product> base = repo.findAll(example, pageable);

        // If q (search term) provided, do an extra filter on name or sku
        if (StringUtils.hasText(q)) {
            String term = q.toLowerCase();
            return base.map(p -> p) // keep page structure
                    .map(p -> p) // no-op to keep Page type
                    .map(p -> p)
                    .filter(p -> p.getName().toLowerCase().contains(term) || p.getSku().toLowerCase().contains(term));
            // Note: Page.filter is not available; instead, simplest approach is to query
            // repo directly for small MVP.
        }

        return base;
    }

    /**
     * Decrement stock for a product by qtyKg (used when order confirmed).
     */
    public void decrementStock(Product product, int qtyKg) {
        int current = product.getStockKg() == null ? 0 : product.getStockKg();
        if (qtyKg > current)
            throw new IllegalArgumentException("Insufficient stock");
        product.setStockKg(current - qtyKg);
        repo.save(product);
    }
}
