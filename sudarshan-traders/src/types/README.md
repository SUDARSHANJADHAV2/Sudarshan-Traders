# TypeScript Definitions

This directory contains shared TypeScript type definitions, interfaces, and enums that are used throughout the application. Centralizing these types helps in maintaining consistency, improving code clarity, and leveraging TypeScript's static typing benefits.

## Organization

- You can create separate files for different domains of types (e.g., `userTypes.ts`, `productTypes.ts`).
- The `index.ts` file in this directory can be used to export types from other files, making it easier to import them elsewhere using a path like `@/types`.

Example:
```typescript
// src/types/userTypes.ts
export interface UserProfile {
  id: string;
  username: string;
  email: string;
}

// src/types/index.ts
export * from './userTypes';
export type SampleGlobalType = { /* ... */ };
```
