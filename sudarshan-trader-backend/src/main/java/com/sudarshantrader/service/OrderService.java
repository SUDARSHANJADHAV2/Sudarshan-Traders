package com.sudarshantrader.service;

import com.sudarshantrader.dto.OrderItemRequest;
import com.sudarshantrader.dto.OrderRequest;
import com.sudarshantrader.entity.*;
import com.sudarshantrader.repository.OrderRepository;
import com.sudarshantrader.repository.ProductRepository;
import com.sudarshantrader.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InvoiceService invoiceService;

    // configurable MOQ (minimum order per SKU), default 10 kg if not set
    private final int globalMoq;

    public OrderService(OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            InvoiceService invoiceService,
            @Value("${app.order.moq:10}") int globalMoq) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.invoiceService = invoiceService;
        this.globalMoq = globalMoq;
    }

    @Transactional
    public Order placeOrder(Long buyerId, OrderRequest req) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (!buyer.isVerified()) {
            throw new RuntimeException("Buyer not verified by admin");
        }

        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        // build order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setPaymentMode(req.getPaymentMode() == null ? "COD" : req.getPaymentMode());
        double subtotal = 0.0;

        for (OrderItemRequest it : req.getItems()) {
            if (it.getQtyKg() == null || it.getQtyKg() <= 0) {
                throw new RuntimeException("Invalid qty for productId: " + it.getProductId());
            }

            // MOQ check
            if (it.getQtyKg() < globalMoq) {
                throw new RuntimeException("Minimum order quantity per SKU is " + globalMoq + " kg");
            }

            Product product = productRepository.findById(it.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + it.getProductId()));

            if (product.getStockKg() == null || product.getStockKg() < it.getQtyKg()) {
                throw new RuntimeException("Insufficient stock for SKU: " + product.getSku());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQtyKg(it.getQtyKg());
            item.setPricePerKg(product.getPricePerKg());
            order.getItems().add(item);

            subtotal += it.getQtyKg() * product.getPricePerKg();
        }

        order.setTotalAmount(subtotal);
        // simple GST example: 5% (change to dynamic later)
        double gst = subtotal * 0.05;
        order.setGstAmount(gst);
        Order saved = orderRepository.save(order);

        // generate invoice now (optional) — invoiceService returns path/URL
        try {
            String invoicePath = invoiceService.generateInvoicePdf(saved);
            saved.setInvoiceUrl(invoicePath);
            saved = orderRepository.save(saved);
        } catch (Exception ex) {
            // fail-safe: log & continue, order is saved
            System.err.println("Invoice generation failed: " + ex.getMessage());
        }

        return saved;
    }

    @Transactional
    public Order updateStatus(Long orderId, Order.Status newStatus) {
        Order ord = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Order.Status prev = ord.getStatus();
        ord.setStatus(newStatus);

        // When status moves to CONFIRMED, decrement inventory
        if (prev != Order.Status.CONFIRMED && newStatus == Order.Status.CONFIRMED) {
            for (OrderItem item : ord.getItems()) {
                Product p = item.getProduct();
                int current = p.getStockKg() == null ? 0 : p.getStockKg();
                if (item.getQtyKg() > current) {
                    throw new RuntimeException("Insufficient stock for SKU during confirm: " + p.getSku());
                }
                p.setStockKg(current - item.getQtyKg());
                productRepository.save(p);
            }
        }

        return orderRepository.save(ord);
    }

    public List<Order> getOrdersForBuyer(Long buyerId) {
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));
        return orderRepository.findByBuyer(buyer);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
}
