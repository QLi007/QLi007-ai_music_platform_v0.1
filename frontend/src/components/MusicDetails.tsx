'use client';

import React, { useEffect, useState } from 'react';
import { MusicService } from '../services/musicService';
import type { Music } from '../types/music';

interface MusicDetailsProps {
  musicId: string;
}

export function MusicDetails({ musicId }: MusicDetailsProps) {
  const [music, setMusic] = useState<Music | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    if (!musicId) {
      setError('无效的音乐ID');
      setLoading(false);
      return;
    }

    const fetchMusicDetails = async () => {
      try {
        setLoading(true);
        const data = await MusicService.getMusicById(musicId);
        setMusic(data);
        setError('');
      } catch (err) {
        setError(err instanceof Error ? err.message : '加载失败');
      } finally {
        setLoading(false);
      }
    };

    fetchMusicDetails();
  }, [musicId]);

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

  if (!music) {
    return null;
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

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold mb-4">{music.title || '未命名音乐'}</h2>
      
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div>
          <h3 className="text-lg font-semibold mb-2">基本信息</h3>
          <div className="space-y-2 text-gray-600">
            <p><span className="font-medium">风格：</span>{getStyleText(music.style)}</p>
            <p><span className="font-medium">时长：</span>{music.duration}秒</p>
            <p><span className="font-medium">状态：</span>{music.status}</p>
            <p><span className="font-medium">创建时间：</span>
              {new Date(music.createdAt).toLocaleString('zh-CN')}
            </p>
          </div>
        </div>
        
        <div>
          <h3 className="text-lg font-semibold mb-2">生成信息</h3>
          <div className="space-y-2 text-gray-600">
            <p><span className="font-medium">提示词：</span>{music.prompt}</p>
            <p><span className="font-medium">更新时间：</span>
              {new Date(music.updatedAt).toLocaleString('zh-CN')}
            </p>
          </div>
        </div>
      </div>

      {music.url && (
        <div className="mt-6">
          <h3 className="text-lg font-semibold mb-2">音乐播放</h3>
          <audio controls className="w-full">
            <source src={music.url} type="audio/mpeg" />
            您的浏览器不支持音频播放。
          </audio>
        </div>
      )}
    </div>
  );
} 