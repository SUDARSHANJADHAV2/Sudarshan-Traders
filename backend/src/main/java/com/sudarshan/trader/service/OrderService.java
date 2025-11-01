package com.sudarshan.trader.service;

import com.sudarshan.trader.dto.*;
import com.sudarshan.trader.entity.*;
import com.sudarshan.trader.exception.*;
import com.sudarshan.trader.repository.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private InvoiceService invoiceService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public OrderResponse createOrder(Long buyerId, OrderRequest request) {
        // Verify buyer is verified
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        if (!buyer.getVerified()) {
            throw new UnauthorizedException("Account pending verification. Please contact admin.");
        }

        // Validate order items not empty
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order items cannot be empty");
        }

        // Calculate order totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // For each order item
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Find product
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: ID " + itemRequest.getProductId()));

            // Check stock
            if (product.getStockKg() < itemRequest.getQtyKg()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Calculate line total
            BigDecimal lineTotal = product.getPricePerKg()
                    .multiply(new BigDecimal(itemRequest.getQtyKg()))
                    .setScale(2, RoundingMode.HALF_UP);

            totalAmount = totalAmount.add(lineTotal);

            // Calculate discount for this item
            BigDecimal discountPercent = discountService.calculateDiscount(
                    product.getSku(), product.getBrand(), itemRequest.getQtyKg());

            BigDecimal itemDiscount = lineTotal
                    .multiply(discountPercent)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            discountAmount = discountAmount.add(itemDiscount);

            // Create order item (will save later after order is saved)
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQtyKg(itemRequest.getQtyKg());
            orderItem.setPricePerKg(product.getPricePerKg());
            orderItem.setLineTotal(lineTotal);
            orderItems.add(orderItem);
        }

        // Calculate GST (18%)
        BigDecimal taxableAmount = totalAmount.subtract(discountAmount);
        BigDecimal gstAmount = taxableAmount
                .multiply(new BigDecimal("0.18"))
                .setScale(2, RoundingMode.HALF_UP);

        // Calculate final amount
        BigDecimal finalAmount = taxableAmount.add(gstAmount);

        // Create Order entity
        com.sudarshan.trader.entity.Order order = new com.sudarshan.trader.entity.Order();
        order.setBuyerId(buyerId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setGstAmount(gstAmount);
        order.setFinalAmount(finalAmount);
        order.setStatus("PLACED");
        order.setPaymentMode(request.getPaymentMode());
        order.setPaymentStatus("PENDING");
        order.setNotes(request.getNotes());

        // Handle online payment (Razorpay)
        if ("ONLINE".equals(request.getPaymentMode())) {
            try {
                RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", finalAmount.multiply(new BigDecimal("100")).intValue()); // Amount in paise
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

                Order razorpayOrder = razorpay.orders.create(orderRequest);
                order.setRazorpayOrderId(razorpayOrder.get("id"));
            } catch (RazorpayException e) {
                throw new BadRequestException("Failed to create Razorpay order: " + e.getMessage());
            }
        }

        // Save order
        order = orderRepository.save(order);

        // Save order items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
        }

        // Generate PDF invoice
        try {
            String invoiceUrl = invoiceService.generateInvoice(order.getId());
            order.setInvoiceUrl(invoiceUrl);
            orderRepository.save(order);
        } catch (Exception e) {
            // Log error but don't fail order creation
            System.err.println("Failed to generate invoice: " + e.getMessage());
        }

        // Build and return response
        return buildOrderResponse(order, orderItems, buyer);
    }

    public void confirmOrder(Long orderId) {
        // Find order
        com.sudarshan.trader.entity.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Decrement stock for all order items
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            int newStock = product.getStockKg() - item.getQtyKg();
            if (newStock < 0) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            product.setStockKg(newStock);
            productRepository.save(product);
        }

        // Update order status
        order.setStatus("CONFIRMED");
        order.setPaymentStatus("PAID");
        orderRepository.save(order);
    }

    public void updateOrderStatus(Long orderId, String newStatus) {
        com.sudarshan.trader.entity.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    public List<OrderResponse> getBuyerOrders(Long buyerId) {
        List<com.sudarshan.trader.entity.Order> orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
        return orders.stream().map(this::buildOrderResponse).toList();
    }

    public List<OrderResponse> getAllOrders() {
        List<com.sudarshan.trader.entity.Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orders.stream().map(this::buildOrderResponse).toList();
    }

    public OrderResponse getOrderById(Long orderId, Long userId, String role) {
        com.sudarshan.trader.entity.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Authorization check
        if ("BUYER".equals(role) && !order.getBuyerId().equals(userId)) {
            throw new UnauthorizedException("Access denied");
        }

        return buildOrderResponse(order);
    }

    public void handlePaymentSuccess(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        // Verify signature
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            if (!hexString.toString().equals(razorpaySignature)) {
                throw new BadRequestException("Invalid payment signature");
            }
        } catch (Exception e) {
            throw new BadRequestException("Payment verification failed: " + e.getMessage());
        }

        // Find order
        com.sudarshan.trader.entity.Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Update payment details
        order.setRazorpayPaymentId(razorpayPaymentId);
        order.setPaymentStatus("PAID");
        orderRepository.save(order);

        // Confirm order (decrement stock)
        confirmOrder(order.getId());
    }

    public void handlePaymentFailure(String razorpayOrderId) {
        com.sudarshan.trader.entity.Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setPaymentStatus("FAILED");
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    private OrderResponse buildOrderResponse(com.sudarshan.trader.entity.Order order) {
        User buyer = userRepository.findById(order.getBuyerId()).orElse(null);
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return buildOrderResponse(order, items, buyer);
    }

    private OrderResponse buildOrderResponse(com.sudarshan.trader.entity.Order order, List<OrderItem> items, User buyer) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setBuyerId(order.getBuyerId());
        if (buyer != null) {
            response.setBuyerCompanyName(buyer.getCompanyName());
            response.setBuyerEmail(buyer.getEmail());
        }
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setGstAmount(order.getGstAmount());
        response.setFinalAmount(order.getFinalAmount());
        response.setStatus(order.getStatus());
        response.setPaymentMode(order.getPaymentMode());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setRazorpayOrderId(order.getRazorpayOrderId());
        response.setRazorpayKey(razorpayKeyId);
        response.setInvoiceUrl(order.getInvoiceUrl());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        // Map order items
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProductId());
            if (product != null) {
                itemResponse.setProductName(product.getName());
                itemResponse.setSku(product.getSku());
            }
            itemResponse.setQtyKg(item.getQtyKg());
            itemResponse.setPricePerKg(item.getPricePerKg());
            itemResponse.setLineTotal(item.getLineTotal());
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);

        return response;
    }
}
