package com.sudarshan.trader.repository;

import com.sudarshan.trader.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByBrand(String brand);

    List<Product> findByVariant(String variant);

    List<Product> findByPackaging(String packaging);

    List<Product> findByBrandAndVariant(String brand, String variant);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByBrandAndVariantAndPackaging(String brand, String variant, String packaging);

    List<Product> findByBrandAndPackaging(String brand, String packaging);

    List<Product> findByVariantAndPackaging(String variant, String packaging);

    Boolean existsBySku(String sku);

    List<Product> findAllByOrderByCreatedAtDesc();
}
