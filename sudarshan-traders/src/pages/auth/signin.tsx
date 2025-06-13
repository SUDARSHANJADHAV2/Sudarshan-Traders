import { getProviders, signIn, ClientSafeProvider, LiteralUnion } from 'next-auth/react';
import { GetServerSidePropsContext } from 'next';
import { BuiltInProviderType } from 'next-auth/providers/index';

interface SignInProps {
  providers: Record<LiteralUnion<BuiltInProviderType, string>, ClientSafeProvider> | null;
}

export default function SignIn({ providers }: SignInProps) {
  return (
    <div style={{ maxWidth: '300px', margin: '100px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '5px' }}>
      <h1>Sign In</h1>
      {providers &&
        Object.values(providers).map((provider) => {
          if (provider.name === "Credentials") { // Special handling for Credentials if needed
            return (
              <div key={provider.name} style={{ marginBottom: '10px' }}>
                {/* Custom form for Credentials provider */}
                <form method="post" action="/api/auth/callback/credentials">
                  {/* CSRF token will be added automatically by NextAuth if not using a custom page with getCsrfToken */}
                  <div>
                    <label htmlFor="email" style={{ display: 'block', marginBottom: '5px' }}>Email</label>
                    <input name="email" id="email" type="email" style={{ width: '100%', padding: '8px', marginBottom: '10px', boxSizing: 'border-box' }}/>
                  </div>
                  <div>
                    <label htmlFor="password" style={{ display: 'block', marginBottom: '5px' }}>Password</label>
                    <input name="password" id="password" type="password" style={{ width: '100%', padding: '8px', marginBottom: '10px', boxSizing: 'border-box' }}/>
                  </div>
                  <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#0070f3', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
                    Sign in with Credentials
                  </button>
                </form>
                <hr style={{ margin: '20px 0' }} />
              </div>
            );
          }
          // Generic button for other providers like Email, Google, etc.
          return (
            <div key={provider.name} style={{ marginBottom: '10px' }}>
              <button
                onClick={() => signIn(provider.id)}
                style={{ width: '100%', padding: '10px', backgroundColor: '#eee', border: '1px solid #ddd', borderRadius: '5px', cursor: 'pointer' }}
              >
                Sign in with {provider.name}
              </button>
            </div>
          );
        })}
    </div>
  );
}

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const providers = await getProviders();
  return {
    props: { providers },
  };
}
