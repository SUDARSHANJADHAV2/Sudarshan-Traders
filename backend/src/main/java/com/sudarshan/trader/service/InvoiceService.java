package com.sudarshan.trader.service;

import com.sudarshan.trader.entity.Order;
import com.sudarshan.trader.entity.OrderItem;
import com.sudarshan.trader.entity.Product;
import com.sudarshan.trader.entity.User;
import com.sudarshan.trader.exception.ResourceNotFoundException;
import com.sudarshan.trader.repository.OrderItemRepository;
import com.sudarshan.trader.repository.OrderRepository;
import com.sudarshan.trader.repository.ProductRepository;
import com.sudarshan.trader.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public String generateInvoice(Long orderId) {
        // Find order with buyer and order items
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User buyer = userRepository.findById(order.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        // Load invoice HTML template
        String htmlContent;
        try {
            ClassPathResource resource = new ClassPathResource("templates/invoice.html");
            htmlContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load invoice template: " + e.getMessage());
        }

        // Replace placeholders with order data
        htmlContent = htmlContent.replace("{{invoiceNumber}}", "INV-" + String.format("%06d", orderId));
        htmlContent = htmlContent.replace("{{orderDate}}", order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        htmlContent = htmlContent.replace("{{buyerCompany}}", buyer.getCompanyName());
        htmlContent = htmlContent.replace("{{buyerContact}}", buyer.getContactPerson());
        htmlContent = htmlContent.replace("{{buyerGst}}", buyer.getGstNumber());
        htmlContent = htmlContent.replace("{{buyerPhone}}", buyer.getPhone());
        htmlContent = htmlContent.replace("{{buyerEmail}}", buyer.getEmail());

        // Build items table
        StringBuilder itemsHtml = new StringBuilder();
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                itemsHtml.append("<tr>")
                        .append("<td>").append(product.getName()).append("</td>")
                        .append("<td>").append(product.getSku()).append("</td>")
                        .append("<td>").append(item.getQtyKg()).append(" kg</td>")
                        .append("<td>₹").append(item.getPricePerKg()).append("</td>")
                        .append("<td>₹").append(item.getLineTotal()).append("</td>")
                        .append("</tr>");
            }
        }
        htmlContent = htmlContent.replace("{{items}}", itemsHtml.toString());

        // Replace totals
        BigDecimal taxableAmount = order.getTotalAmount().subtract(order.getDiscountAmount());
        htmlContent = htmlContent.replace("{{subtotal}}", order.getTotalAmount().toString());
        htmlContent = htmlContent.replace("{{discount}}", order.getDiscountAmount().toString());
        htmlContent = htmlContent.replace("{{taxableAmount}}", taxableAmount.toString());
        htmlContent = htmlContent.replace("{{gst}}", order.getGstAmount().toString());
        htmlContent = htmlContent.replace("{{finalAmount}}", order.getFinalAmount().toString());

        // Convert HTML to PDF using Flying Saucer
        String outputPath = "uploads/invoices/invoice-" + orderId + ".pdf";
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("uploads/invoices"));

            // Generate PDF
            FileOutputStream os = new FileOutputStream(outputPath);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);
            os.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }

        // Return relative path
        String relativePath = "/" + outputPath;

        // Update order with invoice URL
        order.setInvoiceUrl(relativePath);
        orderRepository.save(order);

        return relativePath;
    }
}
