import type { Music, MusicGenerationRequest } from '../types/music';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

export const MusicService = {
  async generateMusic(request: MusicGenerationRequest): Promise<Music> {
    const response = await fetch(`${API_BASE_URL}/music/generate`, {
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
  },

  async getMusicById(id: string): Promise<Music> {
    const response = await fetch(`${API_BASE_URL}/music/${id}`);

    if (!response.ok) {
      throw new Error('获取音乐详情失败');
    }

    return response.json();
  },

  async getMusicList(page: number = 1, size: number = 10) {
    const response = await fetch(`${API_BASE_URL}/music?page=${page - 1}&size=${size}`);

    if (!response.ok) {
      throw new Error('获取音乐列表失败');
    }

    const data = await response.json();
    return {
      items: data.content,
      total: data.totalElements
    };
  }
}; 