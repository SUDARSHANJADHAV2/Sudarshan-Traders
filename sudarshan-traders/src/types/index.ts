// src/types/index.ts

/**
 * Example of a shared TypeScript type.
 * Replace this with actual types as your project develops.
 */
export type SampleType = {
  id: string;
  name: string;
  value?: number;
};

/**
 * This file can be used to export various shared types, interfaces, and enums
 * that are used across different parts of the application.
 *
 * For example:
 * export * from './userTypes';
 * export * from './productTypes';
 * export * from './apiResponseTypes';
 */

// You can also define simple, globally used types directly here:
export interface ApiResponse<T> {
  data: T;
  error?: string | null;
  status: 'success' | 'error';
}
