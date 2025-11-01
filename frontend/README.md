# Sudarshan Trader Frontend

Modern React frontend for B2B Turmeric Wholesale E-commerce platform.

## Tech Stack

- **Framework:** React 18 with Vite
- **Styling:** Tailwind CSS
- **Routing:** React Router DOM
- **State Management:** Context API
- **HTTP Client:** Axios
- **Notifications:** React Hot Toast
- **Payment:** Razorpay

## Features

- Modern turmeric-themed UI (gold, saffron colors)
- Product catalog with filters and search
- Shopping cart with local storage persistence
- Multiple payment modes (COD, Online, Bank Transfer)
- Buyer dashboard (orders, profile)
- Admin dashboard (buyer verification, product management, orders)
- Responsive design (mobile-first)
- Toast notifications

## Prerequisites

- Node.js 18+ and npm
- Backend API running (see backend/README.md)

## Local Setup

### 1. Install dependencies

```bash
npm install
```

### 2. Configure environment

Copy `.env.example` to `.env`:

```bash
cp .env.example .env
```

Update `.env` with your values:
```
VITE_API_URL=http://localhost:8080/api
VITE_RAZORPAY_KEY=rzp_test_xxxxxxxxxx
```

### 3. Run development server

```bash
npm run dev
```

Frontend will be available at `http://localhost:5173`

### 4. Build for production

```bash
npm run build
```

Production build will be in `dist/` directory.

## Project Structure

```
frontend/
├── src/
│   ├── components/       # Reusable components
│   ├── context/          # React Context providers
│   ├── pages/
│   │   ├── public/       # Public pages
│   │   ├── buyer/        # Buyer pages
│   │   └── admin/        # Admin pages
│   ├── services/         # API service layer
│   ├── App.jsx           # Main app with routing
│   └── index.css         # Tailwind CSS
├── package.json
├── tailwind.config.js
└── vite.config.js
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build locally

## User Flows

### Buyer Flow
1. Register account (requires GST number)
2. Wait for admin verification
3. Browse products and add to cart
4. Checkout with COD/Online/Bank Transfer
5. View order history and download invoices

### Admin Flow
1. Login with admin credentials
2. Verify pending buyer registrations
3. Manage products (CRUD operations)
4. View and manage all orders
5. Update order status

## Deployment

### Deploy to Netlify (Free)

1. Connect GitHub repository
2. Build command: `npm run build`
3. Publish directory: `dist`
4. Add environment variables
5. Deploy

## API Integration

Frontend communicates with backend via Axios. All API calls go through service layer in `src/services/`. JWT tokens are automatically attached to authenticated requests via Axios interceptor.

---

Built with ❤️ for Sudarshan Trader
