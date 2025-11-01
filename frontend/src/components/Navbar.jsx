import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

export default function Navbar() {
  const { isAuthenticated, user, logout, isAdmin } = useAuth();
  const { getCartCount } = useCart();

  return (
    <nav className="bg-earthy-brown text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          <Link to="/" className="text-2xl font-bold text-turmeric-gold">
            Sudarshan Trader
          </Link>

          <div className="flex items-center space-x-6">
            <Link to="/products" className="hover:text-turmeric-gold transition">
              Products
            </Link>

            {isAuthenticated ? (
              <>
                {isAdmin ? (
                  <Link to="/admin" className="hover:text-turmeric-gold transition">
                    Admin
                  </Link>
                ) : (
                  <>
                    <Link to="/cart" className="hover:text-turmeric-gold transition relative">
                      Cart
                      {getCartCount() > 0 && (
                        <span className="absolute -top-2 -right-2 bg-saffron text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                          {getCartCount()}
                        </span>
                      )}
                    </Link>
                    <Link to="/orders" className="hover:text-turmeric-gold transition">
                      Orders
                    </Link>
                  </>
                )}
                <Link to="/profile" className="hover:text-turmeric-gold transition">
                  Profile
                </Link>
                <button
                  onClick={logout}
                  className="bg-saffron hover:bg-opacity-90 px-4 py-2 rounded transition"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="hover:text-turmeric-gold transition">
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-turmeric-gold hover:bg-saffron px-4 py-2 rounded transition"
                >
                  Register
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
