import { Inter, Poppins, Noto_Sans_Devanagari } from 'next/font/google';

export const inter = Inter({
  subsets: ['latin'],
  display: 'swap',
  variable: '--font-inter', // CSS variable for Inter
});

export const poppins = Poppins({
  subsets: ['latin'],
  weight: ['400', '600', '700'], // Example weights
  display: 'swap',
  variable: '--font-poppins', // CSS variable for Poppins
});

export const notoDevanagari = Noto_Sans_Devanagari({
  subsets: ['devanagari'],
  weight: ['400', '700'], // Example weights
  display: 'swap',
  variable: '--font-noto-devanagari', // CSS variable for Noto Sans Devanagari
});
