import { useState, useEffect } from 'react';
import { orderService } from '../../services/orderService';
import { adminService } from '../../services/adminService';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';

export default function AdminOrders() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const data = await orderService.getOrders();
      setOrders(data);
    } catch (error) {
      toast.error('Failed to load orders');
    }
  };

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      await adminService.updateOrderStatus(orderId, newStatus);
      toast.success('Order status updated');
      fetchOrders();
    } catch (error) {
      toast.error('Failed to update status');
    }
  };

  const handleConfirmOrder = async (orderId) => {
    try {
      await adminService.confirmOrder(orderId);
      toast.success('Order confirmed and stock decremented');
      fetchOrders();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to confirm order');
    }
  };

  return (
    <div className="page-container">
      <h1 className="section-title">Manage Orders</h1>

      <div className="space-y-4">
        {orders.map(order => (
          <div key={order.orderId} className="card p-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="font-bold text-lg">Order #{order.orderId}</h3>
                <p className="text-sm text-gray-500">
                  {order.buyerCompanyName} - {new Date(order.createdAt).toLocaleDateString()}
                </p>
              </div>
              <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded text-sm font-semibold">
                {order.status}
              </span>
            </div>

            <div className="grid md:grid-cols-3 gap-4 mb-4">
              <div>
                <p className="text-sm text-gray-600">Total Amount</p>
                <p className="font-bold">₹{order.finalAmount}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Payment Mode</p>
                <p className="font-semibold">{order.paymentMode}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Payment Status</p>
                <p className="font-semibold">{order.paymentStatus}</p>
              </div>
            </div>

            <div className="flex gap-4">
              {order.status === 'PLACED' && order.paymentMode !== 'ONLINE' && (
                <button
                  onClick={() => handleConfirmOrder(order.orderId)}
                  className="btn-primary"
                >
                  Confirm Order
                </button>
              )}

              <select
                value={order.status}
                onChange={(e) => handleStatusChange(order.orderId, e.target.value)}
                className="input-field w-auto"
              >
                <option value="PLACED">Placed</option>
                <option value="CONFIRMED">Confirmed</option>
                <option value="SHIPPED">Shipped</option>
                <option value="DELIVERED">Delivered</option>
                <option value="CANCELLED">Cancelled</option>
              </select>

              <Link to={`/orders/${order.orderId}`} className="btn-outline">
                View Details
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
