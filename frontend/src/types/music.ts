export interface Music {
  id: string;
  title: string;
  style: string;
  url: string;
  duration: number;
  createdAt: string;
}

export interface MusicGenerationRequest {
  style: string;
  duration: number;
  title?: string;
}

export interface MusicListResponse {
  items: Music[];
  total: number;
} 