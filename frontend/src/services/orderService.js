import api from './api';

export const orderService = {
  createOrder: async (data) => {
    const response = await api.post('/orders', data);
    return response.data;
  },

  getOrders: async () => {
    const response = await api.get('/orders');
    return response.data;
  },

  getOrderById: async (id) => {
    const response = await api.get(`/orders/${id}`);
    return response.data;
  },

  verifyPayment: async (data) => {
    const response = await api.post('/orders/payment/verify', data);
    return response.data;
  },

  handlePaymentFailure: async (razorpayOrderId) => {
    const response = await api.post('/orders/payment/failure', null, {
      params: { razorpayOrderId },
    });
    return response.data;
  },
};
