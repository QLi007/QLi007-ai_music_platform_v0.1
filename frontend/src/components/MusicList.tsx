'use client';

import React, { useEffect, useState } from 'react';
import { MusicService } from '../services/musicService';
import type { Music } from '../types/music';
import { PAGE_SIZE } from '../constants/pagination';

interface MusicListProps {
  onMusicSelect?: (music: Music) => void;
}

export function MusicList({ onMusicSelect }: MusicListProps) {
  const [musicList, setMusicList] = useState<Music[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const fetchMusic = async () => {
    try {
      setLoading(true);
      const response = await MusicService.getMusicList(page);
      setMusicList(response.items);
      setTotal(response.total);
      setError('');
    } catch (err) {
      setError(err instanceof Error ? err.message : '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMusic();
  }, [page]);

  const handlePrevPage = () => {
    if (page > 1) {
      setPage(page - 1);
    }
  };

  const handleNextPage = () => {
    const totalPages = Math.ceil(total / PAGE_SIZE);
    if (page < totalPages) {
      setPage(page + 1);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
        <span className="ml-2">加载中...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 bg-red-50 border border-red-200 rounded-md text-red-600">
        {error}
      </div>
    );
  }

  if (musicList.length === 0) {
    return (
      <div className="text-center p-8 text-gray-500">
        暂无音乐
      </div>
    );
  }

  const getStyleText = (style: string) => {
    const styleMap: Record<string, string> = {
      'pop': '流行',
      'rock': '摇滚',
      'jazz': '爵士',
      'classical': '古典'
    };
    return styleMap[style] || style;
  };

  const totalPages = Math.ceil(total / PAGE_SIZE);
  const hasNextPage = page < totalPages;
  const hasPrevPage = page > 1;

  return (
    <div className="space-y-4" data-testid="music-list">
      <div className="grid gap-4 md:grid-cols-2">
        {musicList.map(music => (
          <div 
            key={music.id} 
            data-testid={`music-card-${music.id}`}
            className="p-4 bg-white rounded-lg shadow-md cursor-pointer hover:shadow-lg transition-shadow"
            onClick={() => onMusicSelect?.(music)}
          >
            <h3 className="text-lg font-semibold mb-2">{music.title || '未命名音乐'}</h3>
            <div className="space-y-2 text-sm text-gray-600">
              <p><span className="font-medium">风格：</span>{getStyleText(music.style)}</p>
              <p><span className="font-medium">时长：</span>{music.duration}秒</p>
              <p><span className="font-medium">创建时间：</span>
                {new Date(music.createdAt).toLocaleString('zh-CN')}
              </p>
            </div>
          </div>
        ))}
      </div>

      <div className="flex justify-between items-center mt-4">
        <button
          onClick={handlePrevPage}
          disabled={!hasPrevPage}
          className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          上一页
        </button>
        <span className="text-sm text-gray-600">
          第 {page} 页 / 共 {totalPages} 页
        </span>
        <button
          onClick={handleNextPage}
          disabled={!hasNextPage}
          className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          下一页
        </button>
      </div>
    </div>
  );
} 