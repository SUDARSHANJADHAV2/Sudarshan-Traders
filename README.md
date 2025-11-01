# Sudarshan Trader - B2B Turmeric Wholesale E-commerce

Complete full-stack B2B e-commerce platform for wholesale turmeric trading. Built for Sudarshan Trader (Ichalkaranji/Sangli, Maharashtra) to sell premium turmeric under two brands: **Aaditya 511** (heritage premium) and **Sudarshan Gold** (everyday quality), plus loose/unbranded bulk products.

## Project Overview

**Purpose:** Enable kirana stores and wholesale buyers to purchase turmeric in bulk with GST verification, bulk discounts, and multiple payment options.

**Tech Stack:**
- **Frontend:** React 18 + Vite + Tailwind CSS
- **Backend:** Java Spring Boot 3.2 + MySQL
- **Authentication:** JWT with BCrypt
- **Payment Gateway:** Razorpay
- **Deployment:** Netlify (frontend) + Render (backend) - Free tiers

## Key Features

### For Buyers (B2B Customers)
- ✅ Company registration with GST verification
- ✅ Product catalog with brand, variant, and search filters
- ✅ Shopping cart with persistent storage
- ✅ Multiple payment options (COD, Online/Razorpay, Bank Transfer)
- ✅ Bulk discount pricing (automatic based on quantity)
- ✅ Order history with PDF invoice download
- ✅ Account verification workflow

### For Administrators
- ✅ Buyer verification dashboard
- ✅ Product management (CRUD operations)
- ✅ Stock management (kg-based inventory)
- ✅ Order management with status tracking
- ✅ Discount rules configuration
- ✅ CSV export for orders

### Technical Features
- ✅ Role-based access control (ADMIN, BUYER)
- ✅ Secure authentication with JWT tokens
- ✅ Password hashing with BCrypt
- ✅ PDF invoice generation (Flying Saucer)
- ✅ Razorpay payment integration
- ✅ RESTful API design
- ✅ Responsive mobile-first UI
- ✅ Toast notifications
- ✅ CORS configured for production

## Project Structure

```
Sudarshan-Traders/
├── backend/              # Spring Boot REST API
│   ├── src/
│   │   └── main/
│   │       ├── java/com/sudarshan/trader/
│   │       │   ├── config/          # Security, JWT, CORS
│   │       │   ├── controller/      # REST endpoints
│   │       │   ├── dto/             # Data transfer objects
│   │       │   ├── entity/          # JPA entities
│   │       │   ├── exception/       # Error handling
│   │       │   ├── repository/      # Database repositories
│   │       │   └── service/         # Business logic
│   │       └── resources/
│   │           ├── application.properties
│   │           ├── schema.sql       # Database schema
│   │           ├── data.sql         # Seed data
│   │           └── templates/
│   │               └── invoice.html # PDF template
│   ├── uploads/          # File uploads
│   ├── pom.xml
│   └── README.md
│
├── frontend/             # React Vite Application
│   ├── src/
│   │   ├── components/   # Reusable UI components
│   │   ├── context/      # React Context (Auth, Cart)
│   │   ├── pages/
│   │   │   ├── public/   # Public pages
│   │   │   ├── buyer/    # Buyer pages
│   │   │   └── admin/    # Admin pages
│   │   ├── services/     # API service layer
│   │   ├── App.jsx       # Main app with routing
│   │   └── index.css     # Tailwind CSS
│   ├── package.json
│   ├── tailwind.config.js
│   └── README.md
│
└── README.md             # This file
```

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+
- Razorpay account (for payments)

### Backend Setup

```bash
cd backend

# Configure environment variables
cp .env.example .env
# Edit .env with your MySQL and Razorpay credentials

# Build and run
mvn clean install
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`

**Default accounts (change passwords in production):**
- Admin: `admin@sudarshantrader.com` / `Admin@123`
- Test Buyer: `buyer@test.com` / `Buyer@123`

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Configure environment
cp .env.example .env
# Edit .env with backend URL and Razorpay key

# Run development server
npm run dev
```

Frontend runs at `http://localhost:5173`

### Database Setup

```bash
# Create MySQL database
mysql -u root -p
CREATE DATABASE sudarshan_trader;
```

Database schema and seed data are automatically initialized on application startup.

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Key Endpoints

#### Authentication
- `POST /auth/register` - Register new buyer
- `POST /auth/login` - Login and get JWT token
- `GET /auth/me` - Get current user profile

#### Products
- `GET /products` - List all products (with filters)
- `GET /products/{id}` - Get product details
- `POST /products` - Create product (Admin only)
- `PUT /products/{id}` - Update product (Admin only)
- `DELETE /products/{id}` - Delete product (Admin only)

#### Orders
- `POST /orders` - Create new order
- `GET /orders` - List orders (buyer sees own, admin sees all)
- `GET /orders/{id}` - Get order details
- `POST /orders/payment/verify` - Verify Razorpay payment

#### Admin
- `GET /admin/buyers/pending` - List unverified buyers
- `PUT /admin/buyers/{id}/verify` - Verify buyer
- `PUT /admin/orders/{id}/status` - Update order status
- `GET /admin/orders/export` - Export orders to CSV

**For complete API documentation, see `backend/README.md`**

## Deployment

### Backend (Render)

1. Create new Web Service on Render
2. Connect GitHub repository
3. Build command: `cd backend && mvn clean install -DskipTests`
4. Start command: `cd backend && java -jar target/trader-1.0.0.jar`
5. Add environment variables (DB_URL, JWT_SECRET, RAZORPAY keys)
6. Deploy

### Frontend (Netlify)

1. Create new site on Netlify
2. Connect GitHub repository
3. Base directory: `frontend`
4. Build command: `npm run build`
5. Publish directory: `frontend/dist`
6. Add environment variables (VITE_API_URL, VITE_RAZORPAY_KEY)
7. Deploy

### Database (Render/PlanetScale)

Use Render's managed MySQL or PlanetScale free tier. Update `DB_URL` environment variable with connection string.

## Color Palette

- **Turmeric Gold:** `#E4B31B` - Primary brand color
- **Saffron:** `#D97706` - Accent color
- **Earthy Brown:** `#6B4C2A` - Navigation and headers
- **Cream:** `#FFF8E1` - Background

## Sample Data

The application comes with pre-seeded data:
- **Products:** 5 sample products (2 Aaditya 511, 2 Sudarshan Gold, 1 Loose)
- **Discount Rules:** 3 bulk pricing tiers (50kg+, 200kg+, 500kg+)
- **Admin User:** For testing admin features
- **Test Buyer:** For testing buyer flows (verified)

## Security

- Passwords hashed with BCrypt (10 rounds)
- JWT tokens with 24-hour expiry
- CORS configured for allowed origins only
- Environment variables for all secrets
- SQL injection protection via JPA/Hibernate
- XSS protection in React
- HTTPS enforced in production

## License

Proprietary - Built for Sudarshan Trader

## Support

For issues or questions:
- Backend docs: `backend/README.md`
- Frontend docs: `frontend/README.md`

## Acknowledgments

Built with ❤️ for Sudarshan Trader
- Spring Boot, React, Tailwind CSS
- Razorpay for payments
- Flying Saucer for PDF generation

---

**Contact:** info@sudarshantrader.com | +91-9876543210
