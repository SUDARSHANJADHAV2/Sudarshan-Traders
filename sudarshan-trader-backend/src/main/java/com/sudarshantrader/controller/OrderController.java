package com.sudarshantrader.controller;

import com.sudarshantrader.dto.OrderRequest;
import com.sudarshantrader.dto.OrderResponse;
import com.sudarshantrader.entity.Order;
import com.sudarshantrader.entity.User;
import com.sudarshantrader.service.OrderService;
import com.sudarshantrader.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final UserService userService; // from earlier - provides findByEmail()

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Place a new order. Buyer must be authenticated and verified.
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest req, @AuthenticationPrincipal UserDetails ud) {
        if (ud == null)
            return ResponseEntity.status(401).body("Unauthorized");
        User buyer = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order saved = orderService.placeOrder(buyer.getId(), req);
        return ResponseEntity.ok(new OrderResponse(saved));
    }

    /**
     * Buyer: list own orders.
     */
    @GetMapping
    public ResponseEntity<?> listMyOrders(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null)
            return ResponseEntity.status(401).body("Unauthorized");
        User buyer = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderService.getOrdersForBuyer(buyer.getId());
        List<OrderResponse> resp = orders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    /**
     * Admin: list all orders
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> listAllOrders() {
        List<Order> all = orderService.getAllOrders();
        List<OrderResponse> resp = all.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    /**
     * Admin: update order status
     */
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String status = body.get("status");
        if (status == null)
            return ResponseEntity.badRequest().body("Missing status");
        try {
            Order saved = orderService.updateStatus(id, Order.Status.valueOf(status));
            return ResponseEntity.ok(new OrderResponse(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid status value");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Get specific order details (buyer or admin)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        if (ud == null)
            return ResponseEntity.status(401).body("Unauthorized");
        var opt = orderService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();
        Order o = opt.get();
        User requester = userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdmin = ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !o.getBuyer().getId().equals(requester.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(new OrderResponse(o));
    }
}
