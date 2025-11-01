import { Link } from 'react-router-dom';
import { useCart } from '../../context/CartContext';

export default function Cart() {
  const { cart, updateQuantity, removeFromCart, getCartTotal } = useCart();

  if (cart.length === 0) {
    return (
      <div className="page-container text-center py-12">
        <h2 className="text-2xl font-bold mb-4">Your cart is empty</h2>
        <Link to="/products" className="btn-primary">Browse Products</Link>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h1 className="section-title">Shopping Cart</h1>

      <div className="grid lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2">
          {cart.map((item) => (
            <div key={item.product.id} className="card p-4 mb-4">
              <div className="flex gap-4">
                <img
                  src={item.product.imageUrl || '/placeholder.jpg'}
                  alt={item.product.name}
                  className="w-24 h-24 object-cover rounded"
                />

                <div className="flex-1">
                  <h3 className="font-semibold">{item.product.name}</h3>
                  <p className="text-sm text-gray-500">{item.product.brand || 'Loose'}</p>
                  <p className="text-saffron font-bold">₹{item.product.pricePerKg}/kg</p>

                  <div className="flex items-center gap-4 mt-2">
                    <input
                      type="number"
                      min="1"
                      value={item.quantity}
                      onChange={(e) => updateQuantity(item.product.id, parseInt(e.target.value) || 1)}
                      className="w-20 px-2 py-1 border rounded"
                    />
                    <span className="text-sm">kg</span>
                    <button
                      onClick={() => removeFromCart(item.product.id)}
                      className="text-red-600 text-sm hover:underline"
                    >
                      Remove
                    </button>
                  </div>
                </div>

                <div className="text-right">
                  <p className="font-bold text-lg">
                    ₹{(item.product.pricePerKg * item.quantity).toFixed(2)}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="card p-6">
          <h3 className="font-bold text-xl mb-4">Order Summary</h3>
          <div className="space-y-2 mb-4">
            <div className="flex justify-between">
              <span>Subtotal:</span>
              <span>₹{getCartTotal().toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-sm text-gray-600">
              <span>GST (18%):</span>
              <span>₹{(getCartTotal() * 0.18).toFixed(2)}</span>
            </div>
            <div className="border-t pt-2 flex justify-between font-bold text-lg">
              <span>Total:</span>
              <span>₹{(getCartTotal() * 1.18).toFixed(2)}</span>
            </div>
          </div>

          <Link to="/checkout" className="btn-primary w-full text-center block">
            Proceed to Checkout
          </Link>
        </div>
      </div>
    </div>
  );
}
