import type { MessageFilterSpecification } from '@/api/subscription';

export interface MessageFiltersVerification {
  message: string;
  filters: MessageFilterSpecification[];
}

export enum VerificationStatus {
  NOT_MATCHED = 'NOT_MATCHED',
  MATCHED = 'MATCHED',
  ERROR = 'ERROR',
}

export interface MessageFiltersVerificationResponse {
  status: VerificationStatus;
  errorMessage?: string;
}
