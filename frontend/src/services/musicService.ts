import { Music, MusicGenerationRequest, MusicListResponse } from '../types/music';

class MusicServiceClass {
  private baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3001/api';

  async generateMusic(request: MusicGenerationRequest): Promise<Music> {
    const response = await fetch(`${this.baseUrl}/music/generate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error('生成音乐失败');
    }

    return response.json();
  }

  async getMusicList(page: number): Promise<MusicListResponse> {
    const response = await fetch(`${this.baseUrl}/music?page=${page}`);

    if (!response.ok) {
      throw new Error('获取音乐列表失败');
    }

    return response.json();
  }

  async getMusicById(id: string): Promise<Music> {
    const response = await fetch(`${this.baseUrl}/music/${id}`);

    if (!response.ok) {
      throw new Error('获取音乐详情失败');
    }

    return response.json();
  }
}

export const MusicService = new MusicServiceClass(); 