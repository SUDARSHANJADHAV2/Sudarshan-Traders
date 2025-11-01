package com.sudarshan.trader.repository;

import com.sudarshan.trader.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.productId = :productId")
    Boolean existsByProductId(Long productId);
}
