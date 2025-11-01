/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'turmeric-gold': '#E4B31B',
        'saffron': '#D97706',
        'earthy-brown': '#6B4C2A',
        'cream': '#FFF8E1',
      },
    },
  },
  plugins: [],
}
