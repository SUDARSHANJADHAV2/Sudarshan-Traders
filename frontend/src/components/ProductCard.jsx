import { Link } from 'react-router-dom';

export default function ProductCard({ product }) {
  return (
    <div className="card">
      <img
        src={product.imageUrl || '/placeholder.jpg'}
        alt={product.name}
        className="w-full h-48 object-cover"
      />
      <div className="p-4">
        <div className="flex items-center justify-between mb-2">
          <span className="text-xs bg-turmeric-gold text-white px-2 py-1 rounded">
            {product.brand || 'Loose'}
          </span>
          <span className="text-xs text-gray-500">{product.variant}</span>
        </div>

        <h3 className="font-semibold text-lg mb-2 line-clamp-2">{product.name}</h3>

        <p className="text-gray-600 text-sm mb-2 line-clamp-2">{product.description}</p>

        <div className="flex items-center justify-between">
          <div>
            <p className="text-2xl font-bold text-saffron">
              ₹{product.pricePerKg}
              <span className="text-sm text-gray-500">/kg</span>
            </p>
            <p className="text-xs text-gray-500">Stock: {product.stockKg} kg</p>
          </div>

          <Link
            to={`/products/${product.id}`}
            className="btn-primary text-sm py-1 px-4"
          >
            View
          </Link>
        </div>
      </div>
    </div>
  );
}
