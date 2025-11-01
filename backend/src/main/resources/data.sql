-- Sudarshan Trader Seed Data
-- Sample data for development and testing

-- Admin user
-- Email: admin@sudarshantrader.com
-- Password: Admin@123
INSERT INTO users (company_name, contact_person, email, phone, gst_number, password_hash, role, verified) VALUES
('Sudarshan Trader Admin', 'Sudarshan Jadhav', 'admin@sudarshantrader.com', '9876543210', '27AABCS1234F1Z5', '$2a$10$rHQXqGxPEqDnGN5UZ5K4KeOXGG4lLx7KJYuK4l6EJyK3j8K6z9Mhy', 'ADMIN', TRUE);

-- Sample buyer for testing (verified)
-- Email: buyer@test.com
-- Password: Buyer@123
INSERT INTO users (company_name, contact_person, email, phone, gst_number, password_hash, role, verified) VALUES
('Test Kirana Store', 'Rajesh Kumar', 'buyer@test.com', '9123456789', '27AABCT9876G1Z1', '$2a$10$qFx2jgHZYx8K3J5z6JxRTuKLm9z3xK7z5z9z8z7z6z5z4z3z2z1z0', 'BUYER', TRUE);

-- Sample unverified buyer (pending verification)
-- Email: newbuyer@test.com
-- Password: NewBuyer@123
INSERT INTO users (company_name, contact_person, email, phone, gst_number, password_hash, role, verified) VALUES
('New Wholesale Shop', 'Amit Patil', 'newbuyer@test.com', '9234567890', '27AABCN5678H2Z8', '$2a$10$xKz3mKz4mKz5mKz6mKz7mKz8mKz9mKa0mKa1mKa2mKa3mKa4mKa5m', 'BUYER', FALSE);

-- Sample products for both brands and loose powder

-- Aaditya 511 Brand Products
INSERT INTO products (sku, name, brand, variant, packaging, price_per_kg, stock_kg, image_url, description) VALUES
('AAD-P-001', 'Aaditya 511 Turmeric Powder 1kg', 'Aaditya 511', 'POWDER', 'BRANDED', 180.00, 1000, '/uploads/products/aad-powder.jpg', 'Premium heritage brand turmeric powder with high curcumin content. Perfect for traditional cooking and medicinal use.'),
('AAD-W-001', 'Aaditya 511 Whole Turmeric 1kg', 'Aaditya 511', 'WHOLE', 'BRANDED', 160.00, 800, '/uploads/products/aad-whole.jpg', 'Premium whole turmeric roots. Handpicked and sun-dried for maximum freshness and quality.');

-- Sudarshan Gold Brand Products
INSERT INTO products (sku, name, brand, variant, packaging, price_per_kg, stock_kg, image_url, description) VALUES
('SG-P-001', 'Sudarshan Gold Turmeric Powder 1kg', 'Sudarshan Gold', 'POWDER', 'BRANDED', 170.00, 1200, '/uploads/products/sg-powder.jpg', 'Everyday quality turmeric powder. Ideal for high-volume retail and restaurant use.'),
('SG-W-001', 'Sudarshan Gold Whole Turmeric 1kg', 'Sudarshan Gold', 'WHOLE', 'BRANDED', 150.00, 900, '/uploads/products/sg-whole.jpg', 'Quality whole turmeric for daily cooking needs. Great value for money.');

-- Loose/Unbranded Products
INSERT INTO products (sku, name, brand, variant, packaging, price_per_kg, stock_kg, image_url, description) VALUES
('LOOSE-P-001', 'Loose Turmeric Powder (Unbranded)', NULL, 'POWDER', 'LOOSE', 140.00, 2000, '/uploads/products/loose-powder.jpg', 'Unbranded bulk turmeric powder. Perfect for reselling under your own brand or for large-scale commercial use.'),
('LOOSE-W-001', 'Loose Whole Turmeric (Unbranded)', NULL, 'WHOLE', 'LOOSE', 130.00, 1500, '/uploads/products/loose-whole.jpg', 'Bulk whole turmeric without packaging. Cost-effective option for wholesale buyers.');

-- Sample discount rules for bulk pricing
-- Tier 1: 50-199 kg = 5% off
INSERT INTO discount_rules (rule_name, applies_to, min_qty_kg, max_qty_kg, discount_percent, active) VALUES
('Bulk Tier 1 - Small Wholesale', NULL, 50, 199, 5.00, TRUE);

-- Tier 2: 200-499 kg = 10% off
INSERT INTO discount_rules (rule_name, applies_to, min_qty_kg, max_qty_kg, discount_percent, active) VALUES
('Bulk Tier 2 - Medium Wholesale', NULL, 200, 499, 10.00, TRUE);

-- Tier 3: 500+ kg = 15% off
INSERT INTO discount_rules (rule_name, applies_to, min_qty_kg, max_qty_kg, discount_percent, active) VALUES
('Bulk Tier 3 - Large Wholesale', NULL, 500, NULL, 15.00, TRUE);

-- Brand-specific discount example (Aaditya 511 premium - 100+ kg = 3% off)
INSERT INTO discount_rules (rule_name, applies_to, min_qty_kg, max_qty_kg, discount_percent, active) VALUES
('Aaditya 511 Premium Bulk', 'Aaditya 511', 100, NULL, 3.00, TRUE);
