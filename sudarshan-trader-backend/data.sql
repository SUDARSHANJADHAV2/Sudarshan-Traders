-- Admin user (password: admin123) -- change ASAP!
INSERT INTO users (id, company_name, contact_person, email, phone, gst, password_hash, role, verified, created_at)
VALUES (1,'Sudarshan Trader','Sudarshan Jadhav','admin@sudarshantrader.com','0000000000','ADMIN-GST','${bcrypt_admin}', 'ROLE_ADMIN', true, NOW());

-- Sample products
INSERT INTO products (sku, name, brand, variant, packaging, price_per_kg, stock_kg, image_url, created_at)
VALUES
('AAD-P-001','Aaditya 511 Turmeric Powder 1kg','Aaditya 511','powder','branded',180.0,1000,'/images/aaditya_powder.jpg', NOW()),
('AAD-W-001','Aaditya 511 Whole Turmeric 1kg','Aaditya 511','whole','branded',160.0,800,'/images/aaditya_whole.jpg', NOW()),
('SG-P-001','Sudarshan Gold Turmeric Powder 1kg','Sudarshan Gold','powder','branded',170.0,1200,'/images/sg_powder.jpg', NOW()),
('SG-W-001','Sudarshan Gold Whole Turmeric 1kg','Sudarshan Gold','whole','branded',150.0,900,'/images/sg_whole.jpg', NOW()),
('LOOSE-P-001','Loose Turmeric Powder 1kg',NULL,'powder','loose',140.0,2000,'/images/loose_powder.jpg', NOW());
