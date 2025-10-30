package com.sudarshantrader.config;

import com.sudarshantrader.entity.Product;
import com.sudarshantrader.entity.User;
import com.sudarshantrader.repository.ProductRepository;
import com.sudarshantrader.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Seeds a dev/admin user and sample products.
 * Activate by running with 'dev' profile or simply present (not
 * profile-restricted).
 */
@Component
@Profile({ "default", "dev" })
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // create admin if not exists
        String adminEmail = "admin@sudarshantrader.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setCompanyName("Sudarshan Trader");
            admin.setContactPerson("Sudarshan Jadhav");
            admin.setEmail(adminEmail);
            admin.setPhone("9999999999");
            admin.setGst("ADMIN-GST");
            admin.setPasswordHash(passwordEncoder.encode("admin123")); // change in prod
            admin.setRole(User.Role.ADMIN);
            admin.setVerified(true);
            userRepository.save(admin);
            System.out.println("Seeded admin: " + adminEmail + " (password: admin123)");
        }

        // sample products (only add if repo empty)
        if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setSku("AAD-P-001");
            p1.setName("Aaditya 511 Turmeric Powder 1kg");
            p1.setBrand("Aaditya 511");
            p1.setVariant("powder");
            p1.setPackaging("branded");
            p1.setPricePerKg(180.0);
            p1.setStockKg(1000);
            p1.setImageUrl("/images/aaditya_powder.jpg");

            Product p2 = new Product();
            p2.setSku("AAD-W-001");
            p2.setName("Aaditya 511 Whole Turmeric 1kg");
            p2.setBrand("Aaditya 511");
            p2.setVariant("whole");
            p2.setPackaging("branded");
            p2.setPricePerKg(160.0);
            p2.setStockKg(800);
            p2.setImageUrl("/images/aaditya_whole.jpg");

            Product p3 = new Product();
            p3.setSku("SG-P-001");
            p3.setName("Sudarshan Gold Turmeric Powder 1kg");
            p3.setBrand("Sudarshan Gold");
            p3.setVariant("powder");
            p3.setPackaging("branded");
            p3.setPricePerKg(170.0);
            p3.setStockKg(1200);
            p3.setImageUrl("/images/sg_powder.jpg");

            Product p4 = new Product();
            p4.setSku("SG-W-001");
            p4.setName("Sudarshan Gold Whole Turmeric 1kg");
            p4.setBrand("Sudarshan Gold");
            p4.setVariant("whole");
            p4.setPackaging("branded");
            p4.setPricePerKg(150.0);
            p4.setStockKg(900);
            p4.setImageUrl("/images/sg_whole.jpg");

            Product p5 = new Product();
            p5.setSku("LOOSE-P-001");
            p5.setName("Loose Turmeric Powder 1kg");
            p5.setBrand(null);
            p5.setVariant("powder");
            p5.setPackaging("loose");
            p5.setPricePerKg(140.0);
            p5.setStockKg(2000);
            p5.setImageUrl("/images/loose_powder.jpg");

            productRepository.saveAll(List.of(p1, p2, p3, p4, p5));
            System.out.println("Seeded sample products");
        }
    }
}
