package com.sudarshantrader.service;

import com.sudarshantrader.entity.Product;
import com.sudarshantrader.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> search(String name, String category, String brand, Double minPrice,
            int page, int size, String sortBy, String direction) {
        return productRepository.findAll(PageRequest.of(page, size));
    }
}
