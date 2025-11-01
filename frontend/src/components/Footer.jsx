export default function Footer() {
  return (
    <footer className="bg-earthy-brown text-white mt-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h3 className="text-xl font-bold text-turmeric-gold mb-4">Sudarshan Trader</h3>
            <p className="text-sm">Premium B2B Turmeric Wholesale</p>
            <p className="text-sm mt-2">Ichalkaranji / Sangli, Maharashtra</p>
          </div>

          <div>
            <h4 className="font-semibold mb-4">Contact Us</h4>
            <p className="text-sm mb-2">Phone: +91-9876543210</p>
            <p className="text-sm mb-2">Email: info@sudarshantrader.com</p>
            <div className="flex space-x-4 mt-4">
              <a href="https://wa.me/919876543210" target="_blank" rel="noopener noreferrer"
                 className="hover:text-turmeric-gold transition">
                WhatsApp
              </a>
              <a href="https://instagram.com/sudarshantrader" target="_blank" rel="noopener noreferrer"
                 className="hover:text-turmeric-gold transition">
                Instagram
              </a>
            </div>
          </div>

          <div>
            <h4 className="font-semibold mb-4">Our Brands</h4>
            <p className="text-sm mb-2">Aaditya 511 - Premium Heritage</p>
            <p className="text-sm mb-2">Sudarshan Gold - Everyday Quality</p>
            <p className="text-sm mt-4">GST: 27AABCS1234F1Z5</p>
          </div>
        </div>

        <div className="border-t border-gray-600 mt-8 pt-6 text-center text-sm">
          <p>&copy; 2025 Sudarshan Trader. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
