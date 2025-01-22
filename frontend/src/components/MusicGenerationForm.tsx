'use client';

import React, { useState } from 'react';
import { MusicService } from '@/services/musicService';
import type { Music, MusicGenerationRequest } from '@/types/music';

interface MusicGenerationFormProps {
  onSuccess: (music: Music) => void;
  onError: (error: Error) => void;
}

export function MusicGenerationForm({ onSuccess, onError }: MusicGenerationFormProps) {
  const [formData, setFormData] = useState<MusicGenerationRequest>({
    style: '',
    duration: 60,
    title: ''
  });
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    
    try {
      const music = await MusicService.generateMusic(formData);
      onSuccess(music);
    } catch (error) {
      onError(error as Error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'duration' ? parseInt(value) : value
    }));
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 w-full max-w-md">
      <div>
        <label htmlFor="style" className="block text-sm font-medium text-gray-700">
          风格
        </label>
        <select
          id="style"
          name="style"
          value={formData.style}
          onChange={handleChange}
          required
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        >
          <option value="">选择风格</option>
          <option value="pop">流行</option>
          <option value="rock">摇滚</option>
          <option value="jazz">爵士</option>
          <option value="classical">古典</option>
        </select>
      </div>

      <div>
        <label htmlFor="duration" className="block text-sm font-medium text-gray-700">
          时长（秒）
        </label>
        <input
          type="number"
          id="duration"
          name="duration"
          value={formData.duration}
          onChange={handleChange}
          min="30"
          max="300"
          required
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        />
      </div>

      <div>
        <label htmlFor="title" className="block text-sm font-medium text-gray-700">
          标题
        </label>
        <input
          type="text"
          id="title"
          name="title"
          value={formData.title}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        />
      </div>

      <button
        type="submit"
        disabled={isLoading}
        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:bg-gray-400"
      >
        {isLoading ? '生成中...' : '生成音乐'}
      </button>
    </form>
  );
} 