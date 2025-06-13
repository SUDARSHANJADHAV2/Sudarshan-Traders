import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          goldenYellow: '#FFD700',
          goldenYellowDark: '#FFA500', // Added a variant for the second primary color
        },
        secondary: {
          saffron: '#FF8C00',
          saffronDark: '#FF7F00', // Added a variant for the second secondary color
        },
        earthTones: {
          chocolate: '#D2691E',
          peru: '#CD853F',
          burlywood: '#DEB887',
        },
        neutral: {
          beige: '#F5F5DC',
          white: '#FFFFFF',
          jet: '#2C2C2C',
        },
        brand: {
          aditya511: '#SOMECOLOR', // Placeholder
          sudarshanGold: '#ANOTHERCOLOR', // Placeholder
        },
      },
      fontFamily: {
        sans: ['var(--font-inter)', 'sans-serif'],
        poppins: ['var(--font-poppins)', 'sans-serif'], // Using 'poppins' key as requested
        devanagari: ['var(--font-noto-devanagari)', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
export default config
