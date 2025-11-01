import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { productService } from '../../services/productService';
import ProductCard from '../../components/ProductCard';
import toast from 'react-hot-toast';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useSearchParams();

  const brand = searchParams.get('brand');
  const variant = searchParams.get('variant');
  const search = searchParams.get('search');

  useEffect(() => {
    fetchProducts();
  }, [brand, variant, search]);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const data = await productService.getAllProducts({ brand, variant, search });
      setProducts(data);
    } catch (error) {
      toast.error('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key, value) => {
    const params = new URLSearchParams(searchParams);
    if (value) {
      params.set(key, value);
    } else {
      params.delete(key);
    }
    setSearchParams(params);
  };

  return (
    <div className="page-container">
      <h1 className="section-title">Our Products</h1>

      <div className="mb-8 flex flex-wrap gap-4">
        <select
          value={brand || ''}
          onChange={(e) => handleFilterChange('brand', e.target.value)}
          className="input-field w-auto"
        >
          <option value="">All Brands</option>
          <option value="Aaditya 511">Aaditya 511</option>
          <option value="Sudarshan Gold">Sudarshan Gold</option>
        </select>

        <select
          value={variant || ''}
          onChange={(e) => handleFilterChange('variant', e.target.value)}
          className="input-field w-auto"
        >
          <option value="">All Types</option>
          <option value="POWDER">Powder</option>
          <option value="WHOLE">Whole</option>
        </select>

        <input
          type="text"
          placeholder="Search products..."
          value={search || ''}
          onChange={(e) => handleFilterChange('search', e.target.value)}
          className="input-field flex-1"
        />
      </div>

      {loading ? (
        <div className="text-center py-12">Loading products...</div>
      ) : products.length === 0 ? (
        <div className="text-center py-12 text-gray-500">No products found</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </div>
  );
}
