"use client"; // This directive makes it a Client Component

import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation"; // Use next/navigation for app router
import React, { useEffect } from "react";

export default function ProtectedPage() {
  const { data: session, status } = useSession();
  const router = useRouter();

  useEffect(() => {
    // If session is not loading and user is not authenticated, redirect to sign-in page
    // The pages.signIn option in NextAuth config should handle the redirect target automatically
    // or you can specify it manually: router.push('/auth/signin')
    if (status !== "loading" && !session) {
      // next-auth's default behavior with required:true in useSession would handle this,
      // but since we are manually checking status, we can also manually redirect.
      // However, for simplicity and to leverage NextAuth's built-in handling,
      // we can rely on the SessionProvider's behavior or use a more direct check.
      // For now, let's assume pages.signIn in NextAuth config will guide unauth users.
      // A more explicit redirect:
      router.push(process.env.NEXTAUTH_URL ? new URL('/api/auth/signin', process.env.NEXTAUTH_URL).pathname : '/api/auth/signin');
    }
  }, [session, status, router]);

  if (status === "loading") {
    return <p>Loading session...</p>;
  }

  if (!session) {
    // This state might be briefly visible before redirect, or if redirect fails.
    // Or, could return null or a loading spinner while redirecting.
    return <p>Redirecting to sign-in...</p>;
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>Protected Page</h1>
      <p>Welcome, <strong>{session.user?.name || session.user?.email}</strong>!</p>
      <p>This page is protected and you can only see it if you are signed in.</p>
      <p>Your session details:</p>
      <pre style={{ background: '#f5f5f5', padding: '10px', borderRadius: '5px', overflowX: 'auto' }}>
        {JSON.stringify(session, null, 2)}
      </pre>
    </div>
  );
}
