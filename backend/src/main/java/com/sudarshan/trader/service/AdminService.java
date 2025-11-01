package com.sudarshan.trader.service;

import com.sudarshan.trader.entity.Order;
import com.sudarshan.trader.entity.User;
import com.sudarshan.trader.exception.BadRequestException;
import com.sudarshan.trader.exception.ResourceNotFoundException;
import com.sudarshan.trader.repository.OrderRepository;
import com.sudarshan.trader.repository.ProductRepository;
import com.sudarshan.trader.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<User> getPendingBuyers() {
        return userRepository.findByRoleAndVerified("BUYER", false);
    }

    public void verifyBuyer(Long buyerId) {
        User user = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"BUYER".equals(user.getRole())) {
            throw new BadRequestException("User is not a buyer account");
        }

        user.setVerified(true);
        userRepository.save(user);
    }

    public void rejectBuyer(Long buyerId, String reason) {
        User user = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Delete rejected registration
        userRepository.delete(user);
    }

    public List<com.sudarshan.trader.entity.Product> getAllProductsAdmin() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public String exportOrdersCSV() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();

        String fileName = "orders-" + System.currentTimeMillis() + ".csv";
        String outputPath = "uploads/exports/" + fileName;

        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("uploads/exports"));

            FileWriter writer = new FileWriter(outputPath);

            // Write CSV header
            writer.append("Order ID,Date,Buyer Company,Buyer Email,Status,Payment Mode,Total Amount,GST,Final Amount\n");

            // Write data rows
            for (Order order : orders) {
                User buyer = userRepository.findById(order.getBuyerId()).orElse(null);

                writer.append(String.valueOf(order.getId())).append(",");
                writer.append(order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append(",");
                writer.append(buyer != null ? buyer.getCompanyName() : "N/A").append(",");
                writer.append(buyer != null ? buyer.getEmail() : "N/A").append(",");
                writer.append(order.getStatus()).append(",");
                writer.append(order.getPaymentMode()).append(",");
                writer.append(order.getTotalAmount().toString()).append(",");
                writer.append(order.getGstAmount().toString()).append(",");
                writer.append(order.getFinalAmount().toString()).append("\n");
            }

            writer.flush();
            writer.close();

            return "/uploads/exports/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to export orders: " + e.getMessage());
        }
    }
}
