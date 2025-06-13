import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google"; // Geist will be overridden by Inter for sans, but keep mono
import { inter, poppins, notoDevanagari } from "./fonts";
import Providers from "./providers"; // Import the Providers component
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Sudarshan Traders", // Updated title
  description: "Trading platform for Sudarshan Traders", // Updated description
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${inter.variable} ${poppins.variable} ${notoDevanagari.variable} ${geistMono.variable}`}>
      <body
        className={`antialiased`} // geistSans.variable removed as Inter is the new default sans
      >
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
