Problem Statement — Sudarshan Trader

Project: Ichalkaranji / Sangli Turmeric Wholesale E-commerce
Product Name: Sudarshan Trader — B2B Turmeric E-commerce
Brands: Aaditya 511 (premium/heritage) and Sudarshan Gold (everyday/volume)
Prepared for: Sudarshan Jadhav (Sudarshan Trader)
Tech stack: React (Vite) + Tailwind CSS (frontend) · Java (Spring Boot) + JPA/Hibernate (backend) · MySQL (database) · JWT auth · Netlify (frontend hosting) · Render (backend hosting) — free tiers

1. Executive summary / Goal

Build a production-quality B2B e-commerce system to sell wholesale turmeric (powder and whole) under two brands (Aaditya 511 and Sudarshan Gold) and as plain/unbranded loose powder. The platform will allow kirana stores and large wholesale buyers to register, browse brand and loose SKUs priced per kg, place orders (with inventory/stock checks), receive invoices, and communicate via WhatsApp/Instagram links. The website must be modern, visually pleasing (turmeric-inspired theme: warm golds, saffron, earthy brown), responsive, bilingual (English + Marathi), secure, and deployable on free hosting.

2. Stakeholders

Owner / Business: Sudarshan Trader (Sudarshan Jadhav) — manages product, pricing, inventory, admin verification.

Brands: Aaditya 511, Sudarshan Gold — branded SKUs.

Buyers: B2B wholesale customers (kirana stores, big stores).

Admin team: staff who verify buyer GST/docs, manage inventory, handle orders, export invoices.

Developers / Maintainers: you and any future engineers.

3. Scope & primary objectives

Create a robust backend (Spring Boot + MySQL) exposing REST APIs for products, users, orders, admin operations, authentication (JWT).

Create a polished frontend (React + Tailwind) implementing buyer flows and a lightweight admin dashboard.

Support B2B registration (company name, GST, contact person, phone, email), admin verification before purchase.

Manage inventory (stock in kg) and enforce business rules: minimum wholesale quantities (configurable), bulk discounts, per-kg pricing across brands and loose SKUs.

Provide checkout options: Cash on Delivery (COD), UPI/Online, Bank Transfer / Invoice (credit accounts).

Implement PDF invoice generation, WhatsApp/Instagram quick contact integration, and multi-language UI (English + Marathi).

Host frontend on Netlify/Vercel (free) and backend on Render/Railway (free tiers).

Produce deployable code in two public GitHub repositories: sudarshan-trader-backend and sudarshan-trader-frontend.

Ensure secure practices: hashed passwords (BCrypt), JWT for stateless auth, no secrets in repo, CORS, HTTPS in production.

4. Key non-functional requirements

Availability: Acceptable for a small business; free hosting limits documented.

Scalability: Modular architecture so backend and frontend scale independently.

Security: JWT auth, password hashing, environment variables for secrets, role-based admin endpoints.

Performance: CDN for frontend, efficient DB indexes for brand/variant queries.

Usability: Modern, smooth UI with traditional Indian cues; mobile-friendly.

Localization: English and Marathi translations using react-i18next (or equivalent).

Maintainability: Clean repo separation (frontend/backend), documented README, Postman collection, seed SQL, clear folder structure.

5. Functional requirements (detailed)
5.1 User accounts & roles

Roles: ADMIN, BUYER.

Registration flow: Buyer provides company name, contact person, phone, email, GST number, password. New accounts set to PENDING_VERIFICATION (or verified=false). Admin verifies GST/docs before the buyer can place orders.

Login: Email + password returns JWT token with expiry. Optional future OAuth/Google sign-in.

Password storage: BCrypt hashed.

5.2 Product model & catalog

Each product SKU includes:

id, sku, name, brand (Aaditya 511 / Sudarshan Gold / NULL for loose), variant (powder / whole), packaging (branded or loose), pricePerKg (decimal), stockKg (integer), imageUrl, created_at.

Product views/support:

Filter by brand, variant, packaging.

Search by name/SKU.

Price displayed per kg. Add to cart by specifying quantity in kg.

Units: all quantities measured in kilograms.

5.3 Cart & checkout

Buyers add items (kg) to cart.

Cart validates available stockKg before checkout; enforce configurable MOQ (e.g., 10 kg) for wholesale.

Checkout options:

COD

Online/UPI (Razorpay/Stripe/Paytm integration—phase 2)

Bank transfer / Invoice for credit accounts (Net 7/15/30 support for approved buyers).

Order contains: order items (product_id, qty_kg, price_per_kg), total, gst, buyer_id, status (PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED), createdAt, invoice link.

5.4 Admin features

Admin login (role = ADMIN).

Verify new buyers (view GST and uploaded documents). Mark verified.

CRUD for products (create/update/delete brand and loose SKUs).

Manage stock (stockKg) with adjustments (incoming inventory).

View orders, change order status, generate PDF invoice, export orders CSV.

Configure bulk discounts and MOQ thresholds per SKU or brand.

5.5 Inventory & pricing rules

Inventory tracked in Kg (stockKg). When an order is placed and confirmed, stockKg decremented accordingly.

Bulk pricing: configurable discount rules (e.g., 0–49 kg = base price, 50–199 kg = 5% off, 200+ kg = 10% off) — admin configurable.

Loose SKUs: brand = NULL, packaging = 'loose' represent unbranded powder.

5.6 Notifications & messaging

Order confirmation: email (optional free tier) + WhatsApp quick message link containing order summary (https://wa.me/<number>?text=...).

Admin receives new order notification (email/SMS/WhatsApp link).

Product pages contain social links (Instagram), share buttons.

5.7 Localization & accessibility

UI copy stored in JSON for English + Marathi.

Accessible color contrast and keyboard navigation basics.

6. Data model (ER summary)

users (id, company_name, contact_person, email, phone, gst, password_hash, role, verified, created_at)

products (id, sku, name, brand, variant, packaging, price_per_kg, stock_kg, image_url, created_at)

orders (id, buyer_id → users.id, total_amount, gst_amount, status, payment_mode, created_at, invoice_url)

order_items (id, order_id → orders.id, product_id → products.id, qty_kg, price_per_kg)

discount_rules (id, sku_or_brand, min_qty_kg, discount_percent)

audit_logs / inventory_movements (optional) for stock + admin actions

Add indexes: products(brand), products(variant), products(sku).

7. API design (representative endpoints)
Auth

POST /api/auth/register — register buyer (returns pending status)

POST /api/auth/login — returns JWT token

GET /api/auth/me — profile

Products

GET /api/products — list (filters: brand, variant, packaging, q)

GET /api/products/{id} — details

POST /api/products — ADMIN create

PUT /api/products/{id} — ADMIN update

DELETE /api/products/{id} — ADMIN delete

Cart & Orders

POST /api/cart — optional server cart

POST /api/orders — create order (buyer must be verified)

GET /api/orders — buyer order list (admin: all orders)

GET /api/orders/{id} — order details (admin/buyer)

PUT /api/admin/orders/{id}/status — admin update status

Admin

GET /api/admin/buyers?status=pending — list unverified buyers

PUT /api/admin/buyers/{id}/verify — mark verified

GET /api/admin/reports/orders.csv — export

8. Security & auth

JWT tokens (signed with a strong JWT_SECRET environment variable, expiration configurable).

Passwords hashed with BCrypt.

Role checks: Admin endpoints must verify token and role.

CORS configured to allow frontend origin(s).

No secrets committed to GitHub; use environment variables on Render/Netlify.

9. Frontend architecture & UI design

Framework: React (Vite) + Tailwind CSS; routing with react-router-dom.

Key folders: src/components, src/pages, src/services (API), src/context (AuthContext, CartContext), src/i18n.

Pages: Home, Products (catalog with filters), Product Detail, Cart, Checkout, Login/Register, Admin Dashboard (orders + inventory), Profile.

Components: Navbar (with brand colors and language toggle), Footer (WhatsApp/Instagram links), ProductCard, Filters, Pagination, Admin tables.

Theme: Turmeric palette — primary #E4B31B (turmeric gold), accent #D97706 (saffron), earthy #6B4C2A, cream background #FFF8E1. Smooth, modern but with traditional Indian accents.

Images: Use Cloudinary or commit placeholders; real product images uploaded via admin (store image URLs in DB).

10. Dev, repo & deployment plan

Repositories (public):

sudarshan-trader-backend — Spring Boot project, pom.xml, src/, src/main/resources/application.properties.example, data.sql, README.md, Postman collection.

sudarshan-trader-frontend — Vite React project, package.json, src/, tailwind.config.js, README.md, .env.example.

Local dev: Use Docker for MySQL (recommended), or local MySQL. Provide .env and application.properties templates.

Hosting (free tiers):

Frontend → Netlify or Vercel (auto-deploy from GitHub).

Backend → Render or Railway (auto-deploy from GitHub). Add environment vars (SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD, JWT_SECRET, FRONTEND_URL).

DB → Render managed DB / Railway / PlanetScale (free tiers). If using Render’s Postgres, adjust datasource accordingly.

CI/CD: GitHub → auto deploy on pushes to main or release branch. Tests run on push (optional).

11. Sample data & seeds

Seed data.sql should include:

Sample products for both brands and loose powder:

AAD-P-001 Aaditya 511 Turmeric Powder 1kg — branded — price ₹180/kg — stock 1000 kg

AAD-W-001 Aaditya 511 Whole Turmeric 1kg — branded — price ₹160/kg — stock 800 kg

SG-P-001 Sudarshan Gold Turmeric Powder 1kg — branded — price ₹170/kg — stock 1200 kg

SG-W-001 Sudarshan Gold Whole Turmeric 1kg — branded — price ₹150/kg — stock 900 kg

LOOSE-P-001 Loose Turmeric Powder 1kg — loose — price ₹140/kg — stock 2000 kg

Admin user (seeded for testing) — note: change password ASAP.

12. Acceptance criteria / Definition of done

Backend REST APIs implemented and tested (auth, products, orders, admin endpoints).

Frontend pages built and integrated with APIs: product listing, product details, cart, checkout (COD/invoice), login/register.

Buyer registration requires admin verification before placing orders.

Inventory decremented on confirmed orders; backend enforces stock and MOQ.

PDF invoice generation for each order; invoice URL returned in order response.

WhatsApp/Instagram links visible on product and order pages.

Bilingual UI supported and togglable.

Deployed demo on Netlify (frontend) and Render (backend) using free tiers; publicly accessible URLs documented.

All source code pushed to public GitHub repos: sudarshan-trader-backend, sudarshan-trader-frontend, with README and run/deploy instructions.

13. Milestones & roadmap (recommended)

MVP (Week 1–2)

Setup repos, skeleton backend (auth + product APIs), seed DB, basic frontend product list, login/register, cart, checkout → COD/invoice. Deploy MVP.

Phase 2 (Week 3)

Admin dashboard, product CRUD, inventory management, PDF invoices, WhatsApp integration, buyer verification flow.

Phase 3 (Week 4+)

Online payment gateway (Razorpay/Stripe), analytics, bulk upload products, complex discount rules, recommendation engine, mobile app (optional).

14. Constraints & assumptions

Free hosting tiers (Render/Netlify) have limits (idle time, monthly build minutes); suitable for small business but not heavy enterprise traffic. Paid plan recommended once order volume grows.

Bank transfer process is manual unless integrated with accounting/ERP.

WhatsApp integration via wa.me link — full WhatsApp Business API automation requires paid WhatsApp Business setup.

Images should be stored on Cloudinary / S3 / CDN for performance — avoid committing images to Git.

15. Risks & mitigations

Risk: Data loss on free DB — Mitigation: regularly export DB backups; use managed DB backup features or upgrade to paid DB.

Risk: Security misconfiguration — Mitigation: never commit secrets; use environment variables and rotate JWT secret; enforce HTTPS.

Risk: Heavy traffic outgrows free tiers — Mitigation: design modularly so you can move to paid tiers without large refactor.

16. Deliverables

Two public GitHub repos: sudarshan-trader-backend, sudarshan-trader-frontend.

Fully working local dev instructions (step-by-step) + Postman collection.

Live deployed demo links (Netlify + Render) on free tiers.

README and deployment guide, db seed file, and sample admin credentials (to change post-deployment).

Design assets: SVG logos for Aaditya 511 and Sudarshan Gold, color palette & typography suggestions.

17. Appendix / Glossary (quick)

SKU: Stock keeping unit (unique code for product variation).

MOQ: Minimum order quantity (configurable per SKU or global).

B2B: Business-to-Business (kirana stores wholesale buyers).

Loose: Plain/unbranded powder sold without brand labeling.

Branded: Packaged under Aaditya 511 or Sudarshan Gold.

GST: Goods and Services Tax number (admin verification required for B2B buyers).