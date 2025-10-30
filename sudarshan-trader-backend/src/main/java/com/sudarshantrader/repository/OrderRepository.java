package com.sudarshantrader.backend.repository;

import com.sudarshantrader.backend.entity.Order;
import com.sudarshantrader.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(User buyer);
}
