import { useAuth } from '../../context/AuthContext';

export default function Profile() {
  const { user } = useAuth();

  if (!user) return null;

  return (
    <div className="page-container">
      <h1 className="section-title">My Profile</h1>

      <div className="card p-8 max-w-2xl">
        <div className="grid md:grid-cols-2 gap-6">
          <div>
            <label className="label">Company Name</label>
            <p className="font-semibold">{user.companyName}</p>
          </div>

          <div>
            <label className="label">Contact Person</label>
            <p className="font-semibold">{user.contactPerson}</p>
          </div>

          <div>
            <label className="label">Email</label>
            <p className="font-semibold">{user.email}</p>
          </div>

          <div>
            <label className="label">Phone</label>
            <p className="font-semibold">{user.phone}</p>
          </div>

          <div>
            <label className="label">GST Number</label>
            <p className="font-semibold">{user.gstNumber}</p>
          </div>

          <div>
            <label className="label">Account Status</label>
            <p className={`font-semibold ${user.verified ? 'text-green-600' : 'text-orange-600'}`}>
              {user.verified ? 'Verified' : 'Pending Verification'}
            </p>
          </div>
        </div>

        {!user.verified && (
          <div className="mt-6 p-4 bg-orange-50 border border-orange-200 rounded">
            <p className="text-sm text-orange-800">
              Your account is pending verification. You will be able to place orders once admin verifies your account.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
