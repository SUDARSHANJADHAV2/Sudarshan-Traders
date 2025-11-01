import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import toast from 'react-hot-toast';

export default function OrderDetail() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);

  useEffect(() => {
    fetchOrder();
  }, [id]);

  const fetchOrder = async () => {
    try {
      const data = await orderService.getOrderById(id);
      setOrder(data);
    } catch (error) {
      toast.error('Failed to load order');
    }
  };

  if (!order) return <div className="page-container">Loading...</div>;

  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  const invoiceUrl = order.invoiceUrl ? `${API_URL.replace('/api', '')}${order.invoiceUrl}` : null;

  return (
    <div className="page-container">
      <h1 className="section-title">Order #{order.orderId}</h1>

      <div className="grid lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          <div className="card p-6">
            <h3 className="font-bold text-lg mb-4">Order Items</h3>
            {order.items?.map(item => (
              <div key={item.id} className="flex justify-between py-3 border-b">
                <div>
                  <p className="font-semibold">{item.productName}</p>
                  <p className="text-sm text-gray-500">{item.sku}</p>
                  <p className="text-sm">₹{item.pricePerKg}/kg x {item.qtyKg}kg</p>
                </div>
                <p className="font-bold">₹{item.lineTotal}</p>
              </div>
            ))}
          </div>

          {order.notes && (
            <div className="card p-6">
              <h3 className="font-bold mb-2">Notes</h3>
              <p className="text-gray-700">{order.notes}</p>
            </div>
          )}
        </div>

        <div className="space-y-6">
          <div className="card p-6">
            <h3 className="font-bold mb-4">Order Summary</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span>Subtotal:</span>
                <span>₹{order.totalAmount}</span>
              </div>
              {order.discountAmount > 0 && (
                <div className="flex justify-between text-green-600">
                  <span>Discount:</span>
                  <span>-₹{order.discountAmount}</span>
                </div>
              )}
              <div className="flex justify-between">
                <span>GST (18%):</span>
                <span>₹{order.gstAmount}</span>
              </div>
              <div className="border-t pt-2 flex justify-between font-bold text-lg">
                <span>Total:</span>
                <span>₹{order.finalAmount}</span>
              </div>
            </div>
          </div>

          <div className="card p-6">
            <h3 className="font-bold mb-4">Order Info</h3>
            <div className="space-y-2 text-sm">
              <p><span className="font-medium">Status:</span> {order.status}</p>
              <p><span className="font-medium">Payment:</span> {order.paymentMode}</p>
              <p><span className="font-medium">Payment Status:</span> {order.paymentStatus}</p>
              <p><span className="font-medium">Date:</span> {new Date(order.createdAt).toLocaleString()}</p>
            </div>

            {invoiceUrl && (
              <a
                href={invoiceUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="btn-primary w-full mt-4 text-center block"
              >
                Download Invoice
              </a>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
