import { Link } from 'react-router-dom';

export default function AdminDashboard() {
  return (
    <div className="page-container">
      <h1 className="section-title">Admin Dashboard</h1>

      <div className="grid md:grid-cols-3 gap-6">
        <Link to="/admin/buyers" className="card p-8 hover:shadow-2xl transition">
          <div className="text-4xl mb-4">👥</div>
          <h3 className="text-xl font-bold mb-2">Manage Buyers</h3>
          <p className="text-gray-600">Verify new buyer registrations</p>
        </Link>

        <Link to="/admin/products" className="card p-8 hover:shadow-2xl transition">
          <div className="text-4xl mb-4">📦</div>
          <h3 className="text-xl font-bold mb-2">Manage Products</h3>
          <p className="text-gray-600">Add, edit, and manage inventory</p>
        </Link>

        <Link to="/admin/orders" className="card p-8 hover:shadow-2xl transition">
          <div className="text-4xl mb-4">📋</div>
          <h3 className="text-xl font-bold mb-2">Manage Orders</h3>
          <p className="text-gray-600">Track and update order status</p>
        </Link>
      </div>
    </div>
  );
}
