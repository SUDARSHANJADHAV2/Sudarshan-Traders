package com.sudarshantrader.controller;

import com.sudarshantrader.dto.ProductDTO;
import com.sudarshantrader.entity.Product;
import com.sudarshantrader.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    // Public: list products with filters & pagination
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String variant,
            @RequestParam(required = false) String packaging,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        // If q provided, for MVP do a simple approach: fetch page and then filter by q
        // in-memory
        Page<Product> p = svc.search(brand, variant, packaging, null, page, size, sortBy, sortDir);
        if (StringUtils.hasText(q)) {
            String term = q.toLowerCase();
            List<Product> filtered = p.getContent().stream()
                    .filter(prod -> prod.getName().toLowerCase().contains(term)
                            || prod.getSku().toLowerCase().contains(term))
                    .collect(Collectors.toList());
            Page<Product> out = new org.springframework.data.domain.PageImpl<>(filtered, p.getPageable(),
                    filtered.size());
            return ResponseEntity.ok(out);
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return svc.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ADMIN: create product
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody ProductDTO dto) {
        Product p = toEntity(dto);
        Product saved = svc.create(p);
        return ResponseEntity.ok(toDto(saved));
    }

    // ADMIN: update product
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Product p = toEntity(dto);
        Product updated = svc.update(id, p);
        return ResponseEntity.ok(toDto(updated));
    }

    // ADMIN: delete product
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.ok().body(java.util.Map.of("message", "Deleted"));
    }

    // helpers to map DTO <-> entity
    private Product toEntity(ProductDTO dto) {
        Product p = new Product();
        p.setId(dto.getId());
        p.setSku(dto.getSku());
        p.setName(dto.getName());
        p.setBrand(dto.getBrand());
        p.setVariant(dto.getVariant());
        p.setPackaging(dto.getPackaging());
        p.setPricePerKg(dto.getPricePerKg());
        p.setStockKg(dto.getStockKg());
        p.setImageUrl(dto.getImageUrl());
        return p;
    }

    private ProductDTO toDto(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setSku(p.getSku());
        dto.setName(p.getName());
        dto.setBrand(p.getBrand());
        dto.setVariant(p.getVariant());
        dto.setPackaging(p.getPackaging());
        dto.setPricePerKg(p.getPricePerKg());
        dto.setStockKg(p.getStockKg());
        dto.setImageUrl(p.getImageUrl());
        return dto;
    }
}
