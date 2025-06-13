import NextAuth, { NextAuthOptions, User as NextAuthUser } from 'next-auth';
import { PrismaAdapter } from '@next-auth/prisma-adapter';
import CredentialsProvider from 'next-auth/providers/credentials';
import EmailProvider from 'next-auth/providers/email';
import prisma from '@/lib/prisma'; // Adjusted path assuming src/lib/prisma.ts
import bcryptjs from 'bcryptjs';

export const authOptions: NextAuthOptions = {
  adapter: PrismaAdapter(prisma),
  providers: [
    EmailProvider({
      server: {
        host: process.env.EMAIL_SERVER_HOST,
        port: Number(process.env.EMAIL_SERVER_PORT),
        auth: {
          user: process.env.EMAIL_SERVER_USER,
          pass: process.env.EMAIL_SERVER_PASSWORD,
        },
      },
      from: process.env.EMAIL_FROM,
    }),
    CredentialsProvider({
      name: 'Credentials',
      credentials: {
        email: { label: 'Email', type: 'email', placeholder: 'john.doe@example.com' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials.password) {
          return null;
        }

        const user = await prisma.user.findUnique({
          where: { email: credentials.email },
        });

        if (user && user.password_hash) {
          const isValidPassword = bcryptjs.compareSync(credentials.password, user.password_hash);
          if (isValidPassword) {
            // Return a user object that NextAuth expects
            // Ensure all required fields by NextAuth User type are present
            return {
              id: user.id,
              email: user.email,
              name: user.name,
              image: user.image,
              // emailVerified is not directly part of authorize return but handled by adapter
            } as NextAuthUser; // Cast to NextAuthUser
          }
        }
        return null; // Password validation failed or user not found
      },
    }),
  ],
  session: {
    strategy: 'database', // Using database strategy with Prisma adapter
  },
  secret: process.env.NEXTAUTH_SECRET,
  pages: {
    signIn: '/auth/signin', // Optional: Define custom sign-in page
    // error: '/auth/error', // Optional: Custom error page
    // verifyRequest: '/auth/verify-request', // Optional: For Email provider
  },
  callbacks: {
    async session({ session, user }) {
      // Send properties to the client, like an access_token and user id from a provider.
      if (session.user && user) {
        (session.user as any).id = user.id; // Add id to session user object
      }
      return session;
    },
    // JWT callback is not strictly necessary when using database sessions,
    // but can be useful if you plan to use JWTs for other purposes or switch strategies.
    // async jwt({ token, user }) {
    //   if (user) {
    //     token.id = user.id;
    //   }
    //   return token;
    // }
  },
  // Debugging can be enabled during development
  // debug: process.env.NODE_ENV === 'development',
};

export default NextAuth(authOptions);
