export type MusicStatus = 'PENDING' | 'GENERATING' | 'COMPLETED' | 'FAILED';

export interface Music {
  id: string;
  title: string;
  prompt: string;
  style: string;
  duration: number;
  status: MusicStatus;
  url: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface MusicGenerationRequest {
  title: string;
  prompt: string;
  style: string;
  duration: number;
}

export interface PageResponse<T> {
  items: T[];
  total: number;
} 