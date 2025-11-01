import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { orderService } from '../../services/orderService';
import toast from 'react-hot-toast';

export default function Checkout() {
  const { cart, getCartTotal, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [paymentMode, setPaymentMode] = useState('COD');
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);

  if (!user?.verified) {
    return (
      <div className="page-container text-center py-12">
        <h2 className="text-2xl font-bold text-red-600 mb-4">Account Pending Verification</h2>
        <p>Please wait for admin to verify your account before placing orders.</p>
      </div>
    );
  }

  const handlePlaceOrder = async () => {
    try {
      setLoading(true);
      const items = cart.map(item => ({
        productId: item.product.id,
        qtyKg: item.quantity
      }));

      const orderData = { items, paymentMode, notes };
      const order = await orderService.createOrder(orderData);

      if (paymentMode === 'ONLINE' && order.razorpayOrderId) {
        // Initialize Razorpay
        const options = {
          key: order.razorpayKey,
          amount: order.finalAmount * 100,
          order_id: order.razorpayOrderId,
          name: 'Sudarshan Trader',
          description: `Order #${order.orderId}`,
          handler: async (response) => {
            try {
              await orderService.verifyPayment({
                razorpayOrderId: response.razorpay_order_id,
                razorpayPaymentId: response.razorpay_payment_id,
                razorpaySignature: response.razorpay_signature
              });
              clearCart();
              toast.success('Order placed successfully!');
              navigate(`/orders/${order.orderId}`);
            } catch (error) {
              toast.error('Payment verification failed');
            }
          }
        };

        const razorpay = new window.Razorpay(options);
        razorpay.open();
      } else {
        clearCart();
        toast.success('Order placed successfully!');
        navigate(`/orders/${order.orderId}`);
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to place order');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <h1 className="section-title">Checkout</h1>

      <div className="grid lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2">
          <div className="card p-6 mb-6">
            <h3 className="font-bold text-xl mb-4">Order Items</h3>
            {cart.map(item => (
              <div key={item.product.id} className="flex justify-between py-2 border-b">
                <span>{item.product.name} x {item.quantity}kg</span>
                <span className="font-semibold">₹{(item.product.pricePerKg * item.quantity).toFixed(2)}</span>
              </div>
            ))}
          </div>

          <div className="card p-6">
            <h3 className="font-bold text-xl mb-4">Payment Method</h3>
            <div className="space-y-3">
              <label className="flex items-center space-x-3">
                <input
                  type="radio"
                  name="payment"
                  value="COD"
                  checked={paymentMode === 'COD'}
                  onChange={(e) => setPaymentMode(e.target.value)}
                />
                <span>Cash on Delivery (COD)</span>
              </label>
              <label className="flex items-center space-x-3">
                <input
                  type="radio"
                  name="payment"
                  value="ONLINE"
                  checked={paymentMode === 'ONLINE'}
                  onChange={(e) => setPaymentMode(e.target.value)}
                />
                <span>Online Payment (Razorpay)</span>
              </label>
              <label className="flex items-center space-x-3">
                <input
                  type="radio"
                  name="payment"
                  value="BANK_TRANSFER"
                  checked={paymentMode === 'BANK_TRANSFER'}
                  onChange={(e) => setPaymentMode(e.target.value)}
                />
                <span>Bank Transfer / Invoice</span>
              </label>
            </div>

            <div className="mt-4">
              <label className="label">Additional Notes (Optional)</label>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                className="input-field"
                rows="3"
              />
            </div>
          </div>
        </div>

        <div className="card p-6">
          <h3 className="font-bold text-xl mb-4">Order Total</h3>
          <div className="space-y-2 mb-6">
            <div className="flex justify-between">
              <span>Subtotal:</span>
              <span>₹{getCartTotal().toFixed(2)}</span>
            </div>
            <div className="flex justify-between">
              <span>GST (18%):</span>
              <span>₹{(getCartTotal() * 0.18).toFixed(2)}</span>
            </div>
            <div className="border-t pt-2 flex justify-between font-bold text-lg">
              <span>Total:</span>
              <span>₹{(getCartTotal() * 1.18).toFixed(2)}</span>
            </div>
          </div>

          <button
            onClick={handlePlaceOrder}
            disabled={loading}
            className="btn-primary w-full"
          >
            {loading ? 'Placing Order...' : 'Place Order'}
          </button>
        </div>
      </div>
    </div>
  );
}
