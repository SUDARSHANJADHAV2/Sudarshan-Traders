package com.sudarshan.trader.repository;

import com.sudarshan.trader.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerId(Long buyerId);

    List<Order> findByStatus(String status);

    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    List<Order> findAllByOrderByCreatedAtDesc();

    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    List<Order> findByPaymentMode(String paymentMode);

    List<Order> findByPaymentStatus(String paymentStatus);
}
