// src/pages/api/test-db.ts
import { NextApiRequest, NextApiResponse } from 'next';
import prisma from '@/lib/prisma';

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  if (req.method !== 'GET') {
    return res.status(405).json({ 
      status: 'error', 
      message: 'Method not allowed. Use GET.' 
    });
  }

  try {
    // Test database connection by counting users
    const userCount = await prisma.user.count();
    
    return res.status(200).json({
      status: 'success',
      message: 'Successfully connected to the database and fetched user count.',
      userCount: userCount
    });
  } catch (error) {
    console.error('Database connection error:', error);
    
    return res.status(500).json({
      status: 'error',
      message: 'Failed to connect to the database.',
      error: error instanceof Error ? error.message : 'Unknown error'
    });
  }
}

