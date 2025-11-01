import api from './api';

export const adminService = {
  // Buyers
  getPendingBuyers: async () => {
    const response = await api.get('/admin/buyers/pending');
    return response.data;
  },

  verifyBuyer: async (id) => {
    const response = await api.put(`/admin/buyers/${id}/verify`);
    return response.data;
  },

  rejectBuyer: async (id, reason) => {
    const response = await api.delete(`/admin/buyers/${id}/reject`, {
      params: { reason },
    });
    return response.data;
  },

  // Products
  getAllProducts: async () => {
    const response = await api.get('/admin/products');
    return response.data;
  },

  // Orders
  updateOrderStatus: async (id, status) => {
    const response = await api.put(`/admin/orders/${id}/status`, null, {
      params: { status },
    });
    return response.data;
  },

  confirmOrder: async (id) => {
    const response = await api.put(`/admin/orders/${id}/confirm`);
    return response.data;
  },

  exportOrders: async () => {
    const response = await api.get('/admin/orders/export');
    return response.data;
  },

  // Discounts
  getAllDiscounts: async () => {
    const response = await api.get('/admin/discounts');
    return response.data;
  },

  createDiscount: async (data) => {
    const response = await api.post('/admin/discounts', data);
    return response.data;
  },

  updateDiscount: async (id, data) => {
    const response = await api.put(`/admin/discounts/${id}`, data);
    return response.data;
  },

  deleteDiscount: async (id) => {
    await api.delete(`/admin/discounts/${id}`);
  },
};
