"use client"; // Make this a client component as it uses useSession hook

import { useSession, signIn, signOut } from 'next-auth/react';

export default function AuthStatus() {
  const { data: session, status } = useSession();

  if (status === 'loading') return <p>Loading...</p>;

  if (session) {
    return (
      <div style={{ padding: '20px', border: '1px solid #eee', borderRadius: '5px', marginTop: '20px' }}>
        <p>Signed in as: <strong>{session.user?.email || session.user?.name || 'Unknown'}</strong></p>
        {/* Type assertion to access custom id property if added to session user */}
        <p>User ID: {(session.user as any)?.id || 'Not available'}</p>
        <button
          onClick={() => signOut()}
          style={{ padding: '8px 15px', backgroundColor: '#ff4d4d', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
        >
          Sign out
        </button>
      </div>
    );
  }
  return (
    <div style={{ padding: '20px', border: '1px solid #eee', borderRadius: '5px', marginTop: '20px' }}>
      <p>Not signed in</p>
      <button
        onClick={() => signIn()} // This will redirect to NextAuth.js default sign-in page or pages.signIn if configured
        style={{ padding: '8px 15px', backgroundColor: '#0070f3', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
      >
        Sign in
      </button>
    </div>
  );
}
