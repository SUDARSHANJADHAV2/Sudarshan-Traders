import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { productService } from '../../services/productService';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

export default function ProductDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(10);
  const { addToCart } = useCart();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    fetchProduct();
  }, [id]);

  const fetchProduct = async () => {
    try {
      const data = await productService.getProductById(id);
      setProduct(data);
    } catch (error) {
      toast.error('Failed to load product');
    }
  };

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      toast.error('Please login to add items to cart');
      return;
    }
    if (quantity > product.stockKg) {
      toast.error('Insufficient stock');
      return;
    }
    addToCart(product, quantity);
  };

  if (!product) return <div className="page-container">Loading...</div>;

  return (
    <div className="page-container">
      <div className="grid md:grid-cols-2 gap-12">
        <img
          src={product.imageUrl || '/placeholder.jpg'}
          alt={product.name}
          className="w-full h-96 object-cover rounded-lg"
        />

        <div>
          <span className="bg-turmeric-gold text-white px-3 py-1 rounded text-sm">
            {product.brand || 'Loose'} - {product.variant}
          </span>
          <h1 className="text-4xl font-bold text-earthy-brown mt-4 mb-4">{product.name}</h1>
          <p className="text-gray-600 mb-6">{product.description}</p>

          <div className="mb-6">
            <p className="text-3xl font-bold text-saffron">
              ₹{product.pricePerKg}<span className="text-lg">/kg</span>
            </p>
            <p className="text-sm text-gray-500 mt-1">Stock available: {product.stockKg} kg</p>
          </div>

          <div className="mb-6">
            <label className="label">Quantity (kg)</label>
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
              className="input-field"
            />
            <p className="text-sm text-gray-500 mt-2">
              Total: ₹{(product.pricePerKg * quantity).toFixed(2)}
            </p>
          </div>

          <button onClick={handleAddToCart} className="btn-primary w-full">
            Add to Cart
          </button>

          <div className="mt-8 border-t pt-6">
            <h3 className="font-semibold mb-4">Product Details</h3>
            <div className="space-y-2 text-sm">
              <p><span className="font-medium">SKU:</span> {product.sku}</p>
              <p><span className="font-medium">Packaging:</span> {product.packaging}</p>
              <p><span className="font-medium">Brand:</span> {product.brand || 'Unbranded'}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
