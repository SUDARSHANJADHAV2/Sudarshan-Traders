import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <div>
      <div className="bg-gradient-to-r from-saffron to-turmeric-gold text-white py-20">
        <div className="page-container text-center">
          <h1 className="text-5xl font-bold mb-6">Premium Turmeric Wholesale</h1>
          <p className="text-xl mb-8">Aaditya 511 & Sudarshan Gold - Trusted Quality Since Generations</p>
          <Link to="/products" className="bg-white text-saffron px-8 py-3 rounded-lg font-semibold hover:bg-opacity-90 transition inline-block">
            Browse Products
          </Link>
        </div>
      </div>

      <div className="page-container">
        <div className="grid md:grid-cols-2 gap-12 my-16">
          <div className="card p-8">
            <h2 className="text-3xl font-bold text-earthy-brown mb-4">Aaditya 511</h2>
            <p className="text-gray-700 mb-4">
              Premium heritage brand turmeric with high curcumin content. Perfect for traditional cooking and medicinal use.
            </p>
            <Link to="/products?brand=Aaditya 511" className="btn-primary">
              Explore Aaditya 511
            </Link>
          </div>

          <div className="card p-8">
            <h2 className="text-3xl font-bold text-earthy-brown mb-4">Sudarshan Gold</h2>
            <p className="text-gray-700 mb-4">
              Everyday quality turmeric. Ideal for high-volume retail and restaurant use. Great value for money.
            </p>
            <Link to="/products?brand=Sudarshan Gold" className="btn-primary">
              Explore Sudarshan Gold
            </Link>
          </div>
        </div>

        <div className="my-16">
          <h2 className="section-title text-center">Why Choose Us?</h2>
          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center p-6">
              <div className="text-4xl mb-4">✓</div>
              <h3 className="font-semibold text-xl mb-2">Premium Quality</h3>
              <p className="text-gray-600">Sourced directly from Sangli farms</p>
            </div>
            <div className="text-center p-6">
              <div className="text-4xl mb-4">✓</div>
              <h3 className="font-semibold text-xl mb-2">Bulk Discounts</h3>
              <p className="text-gray-600">Better prices for larger quantities</p>
            </div>
            <div className="text-center p-6">
              <div className="text-4xl mb-4">✓</div>
              <h3 className="font-semibold text-xl mb-2">Fast Delivery</h3>
              <p className="text-gray-600">Quick dispatch across Maharashtra</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
