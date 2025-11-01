import { useState, useEffect } from 'react';
import { adminService } from '../../services/adminService';
import toast from 'react-hot-toast';

export default function AdminBuyers() {
  const [pendingBuyers, setPendingBuyers] = useState([]);

  useEffect(() => {
    fetchPendingBuyers();
  }, []);

  const fetchPendingBuyers = async () => {
    try {
      const data = await adminService.getPendingBuyers();
      setPendingBuyers(data);
    } catch (error) {
      toast.error('Failed to load buyers');
    }
  };

  const handleVerify = async (id) => {
    try {
      await adminService.verifyBuyer(id);
      toast.success('Buyer verified successfully');
      fetchPendingBuyers();
    } catch (error) {
      toast.error('Failed to verify buyer');
    }
  };

  const handleReject = async (id) => {
    if (!confirm('Are you sure you want to reject this buyer?')) return;
    try {
      await adminService.rejectBuyer(id, 'Rejected by admin');
      toast.success('Buyer rejected');
      fetchPendingBuyers();
    } catch (error) {
      toast.error('Failed to reject buyer');
    }
  };

  return (
    <div className="page-container">
      <h1 className="section-title">Pending Buyer Verifications</h1>

      {pendingBuyers.length === 0 ? (
        <p className="text-center text-gray-500 py-12">No pending verifications</p>
      ) : (
        <div className="space-y-4">
          {pendingBuyers.map(buyer => (
            <div key={buyer.id} className="card p-6">
              <div className="grid md:grid-cols-2 gap-4 mb-4">
                <div>
                  <p className="label">Company</p>
                  <p className="font-semibold">{buyer.companyName}</p>
                </div>
                <div>
                  <p className="label">Contact Person</p>
                  <p className="font-semibold">{buyer.contactPerson}</p>
                </div>
                <div>
                  <p className="label">Email</p>
                  <p className="font-semibold">{buyer.email}</p>
                </div>
                <div>
                  <p className="label">Phone</p>
                  <p className="font-semibold">{buyer.phone}</p>
                </div>
                <div>
                  <p className="label">GST Number</p>
                  <p className="font-semibold">{buyer.gstNumber}</p>
                </div>
                <div>
                  <p className="label">Registered</p>
                  <p className="font-semibold">{new Date(buyer.createdAt).toLocaleDateString()}</p>
                </div>
              </div>

              <div className="flex gap-4">
                <button
                  onClick={() => handleVerify(buyer.id)}
                  className="btn-primary"
                >
                  Verify
                </button>
                <button
                  onClick={() => handleReject(buyer.id)}
                  className="bg-red-600 text-white px-6 py-2 rounded hover:bg-red-700"
                >
                  Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
