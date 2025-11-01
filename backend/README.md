# Sudarshan Trader Backend

B2B Turmeric Wholesale E-commerce Backend API built with Spring Boot.

## Tech Stack

- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** MySQL
- **Authentication:** JWT (JSON Web Tokens)
- **Security:** Spring Security with BCrypt password hashing
- **Payment Gateway:** Razorpay
- **PDF Generation:** Flying Saucer (xhtmlrenderer)
- **Build Tool:** Maven

## Features

- User authentication and authorization (JWT)
- Role-based access control (ADMIN, BUYER)
- Product catalog management
- Bulk discount pricing rules
- Order management with multiple payment modes (COD, Online, Bank Transfer)
- Razorpay payment integration
- PDF invoice generation
- Admin dashboard APIs
- Buyer verification workflow
- Stock management
- CSV export for orders

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Razorpay account (for online payments)

## Local Setup

### 1. Clone the repository

```bash
cd backend
```

### 2. Configure MySQL Database

Create a MySQL database:

```sql
CREATE DATABASE sudarshan_trader;
```

### 3. Configure Environment Variables

Copy `.env.example` to `.env` or set environment variables:

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/sudarshan_trader
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# JWT Secret (change in production!)
JWT_SECRET=your-strong-secret-key-min-256-bits

# Razorpay (get from https://dashboard.razorpay.com/)
RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxx
RAZORPAY_KEY_SECRET=your_razorpay_secret

# Frontend URL for CORS
FRONTEND_URL=http://localhost:5173
```

### 4. Build and Run

```bash
# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### 5. Database Initialization

The application automatically:
- Creates tables on startup (via `schema.sql`)
- Seeds initial data (via `data.sql`)

**Default accounts:**
- **Admin:** `admin@sudarshantrader.com` / `Admin@123`
- **Test Buyer:** `buyer@test.com` / `Buyer@123`

**⚠️ IMPORTANT:** Change default passwords in production!

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register Buyer
```http
POST /api/auth/register
Content-Type: application/json

{
  "companyName": "Test Company",
  "contactPerson": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "gstNumber": "27AABCT1234F1Z9",
  "password": "Password@123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "buyer@test.com",
  "password": "Buyer@123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 2,
    "email": "buyer@test.com",
    "companyName": "Test Kirana Store",
    "role": "BUYER",
    "verified": true
  }
}
```

#### Get Current User
```http
GET /api/auth/me
Authorization: Bearer {token}
```

### Product Endpoints

#### Get All Products (Public)
```http
GET /api/products
GET /api/products?brand=Aaditya 511&variant=POWDER
GET /api/products?search=turmeric
```

#### Get Product by ID
```http
GET /api/products/1
```

#### Create Product (Admin Only)
```http
POST /api/products
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "sku": "TEST-P-001",
  "name": "Test Product",
  "brand": "Aaditya 511",
  "variant": "POWDER",
  "packaging": "BRANDED",
  "pricePerKg": 180.00,
  "stockKg": 1000,
  "imageUrl": "/uploads/products/test.jpg",
  "description": "Test product description"
}
```

#### Update Product (Admin Only)
```http
PUT /api/products/1
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "pricePerKg": 190.00,
  "stockKg": 1200
}
```

#### Delete Product (Admin Only)
```http
DELETE /api/products/1
Authorization: Bearer {admin_token}
```

#### Upload Product Image (Admin Only)
```http
POST /api/products/upload-image
Authorization: Bearer {admin_token}
Content-Type: multipart/form-data

image: [file]

Response:
{
  "imageUrl": "/uploads/products/product-1234567890-image.jpg"
}
```

### Order Endpoints

#### Create Order (Authenticated Buyer)
```http
POST /api/orders
Authorization: Bearer {buyer_token}
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "qtyKg": 100
    },
    {
      "productId": 3,
      "qtyKg": 50
    }
  ],
  "paymentMode": "COD",
  "notes": "Please deliver before Friday"
}

Response:
{
  "orderId": 1,
  "totalAmount": 24000.00,
  "discountAmount": 1200.00,
  "gstAmount": 4104.00,
  "finalAmount": 26904.00,
  "status": "PLACED",
  "paymentStatus": "PENDING",
  "invoiceUrl": "/uploads/invoices/invoice-1.pdf",
  "items": [...],
  "createdAt": "2025-11-01T10:30:00"
}
```

#### Get Orders
```http
GET /api/orders
Authorization: Bearer {token}

// Buyers see only their orders
// Admins see all orders
```

#### Get Order by ID
```http
GET /api/orders/1
Authorization: Bearer {token}
```

#### Verify Payment (Online Payment)
```http
POST /api/orders/payment/verify
Authorization: Bearer {buyer_token}
Content-Type: application/json

{
  "razorpayOrderId": "order_xyz123",
  "razorpayPaymentId": "pay_abc456",
  "razorpaySignature": "signature_hash"
}
```

### Admin Endpoints

#### Get Pending Buyers
```http
GET /api/admin/buyers/pending
Authorization: Bearer {admin_token}
```

#### Verify Buyer
```http
PUT /api/admin/buyers/2/verify
Authorization: Bearer {admin_token}
```

#### Reject Buyer
```http
DELETE /api/admin/buyers/3/reject?reason=Invalid GST
Authorization: Bearer {admin_token}
```

#### Update Order Status
```http
PUT /api/admin/orders/1/status?status=SHIPPED
Authorization: Bearer {admin_token}
```

#### Confirm Order
```http
PUT /api/admin/orders/1/confirm
Authorization: Bearer {admin_token}
```

#### Export Orders to CSV
```http
GET /api/admin/orders/export
Authorization: Bearer {admin_token}

Response:
{
  "message": "Orders exported successfully",
  "filePath": "/uploads/exports/orders-1234567890.csv"
}
```

#### Manage Discount Rules
```http
GET /api/admin/discounts
POST /api/admin/discounts
PUT /api/admin/discounts/1
DELETE /api/admin/discounts/1
Authorization: Bearer {admin_token}
```

## Project Structure

```
backend/
├── src/
│   └── main/
│       ├── java/com/sudarshan/trader/
│       │   ├── SudarshanTraderApplication.java
│       │   ├── config/           # Security, JWT, CORS, Static files
│       │   ├── controller/       # REST API endpoints
│       │   ├── dto/              # Data Transfer Objects
│       │   ├── entity/           # JPA entities
│       │   ├── exception/        # Custom exceptions & handler
│       │   ├── repository/       # JPA repositories
│       │   └── service/          # Business logic
│       └── resources/
│           ├── application.properties
│           ├── schema.sql        # Database schema
│           ├── data.sql          # Seed data
│           └── templates/
│               └── invoice.html  # PDF invoice template
├── uploads/                      # File uploads directory
│   ├── products/
│   ├── invoices/
│   └── exports/
├── pom.xml
└── README.md
```

## Database Schema

- **users** - Admin and buyer accounts
- **products** - Turmeric products (branded and loose)
- **orders** - Customer orders
- **order_items** - Individual items in orders
- **discount_rules** - Bulk pricing discount rules

## Security

- Passwords are hashed using BCrypt
- JWT tokens expire after 24 hours
- Role-based access control (ADMIN, BUYER)
- CORS configured for frontend origin
- Environment variables for secrets

## Production Deployment

### 1. Update application.properties

```properties
spring.sql.init.mode=never  # Don't re-create schema in production
jwt.secret=${JWT_SECRET}    # Strong secret from environment
```

### 2. Deploy to Render/Railway

1. Connect GitHub repository
2. Set environment variables
3. Set start command: `java -jar target/trader-1.0.0.jar`
4. Deploy

### 3. Production Checklist

- [ ] Change default admin password
- [ ] Set strong JWT secret
- [ ] Configure production database
- [ ] Set production Razorpay keys
- [ ] Configure frontend URL for CORS
- [ ] Enable HTTPS
- [ ] Set up database backups

## Troubleshooting

### Database Connection Error
```
Check DB_URL, DB_USERNAME, DB_PASSWORD environment variables
Ensure MySQL is running and database exists
```

### JWT Token Expired
```
Tokens expire after 24 hours
Users need to login again
```

### Razorpay Integration Issues
```
Verify RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET
Check Razorpay dashboard for test mode keys
```

### File Upload Fails
```
Ensure uploads/ directory exists and is writable
Check file size limit (5MB max)
```

## Support

For issues or questions:
- Check API documentation above
- Review error messages in console logs
- Verify environment variables are set correctly

---

Built with ❤️ for Sudarshan Trader
