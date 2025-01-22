'use client';

import React, { useState } from 'react';
import { MusicGenerationForm } from '@/components/MusicGenerationForm';
import { MusicList } from '@/components/MusicList';
import type { Music } from '@/types/music';

export default function Home() {
  const [generatedMusic, setGeneratedMusic] = useState<Music | null>(null);
  const [error, setError] = useState<string>('');

  const handleSuccess = (music: Music) => {
    setGeneratedMusic(music);
    setError('');
  };

  const handleError = (error: Error) => {
    setError(error.message);
    setGeneratedMusic(null);
  };

  return (
    <main className="flex min-h-screen flex-col items-center p-24">
      <h1 className="text-4xl font-bold mb-8">AI 音乐平台</h1>
      
      <div className="w-full max-w-4xl">
        <div className="mb-8">
          <MusicGenerationForm 
            onSuccess={handleSuccess}
            onError={handleError}
          />

          {error && (
            <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-md text-red-600">
              {error}
            </div>
          )}

          {generatedMusic && (
            <div className="mt-8 p-6 bg-white shadow-lg rounded-lg">
              <h2 className="text-2xl font-semibold mb-4">{generatedMusic.title || '新生成的音乐'}</h2>
              <div className="space-y-2">
                <p><span className="font-medium">风格：</span>{generatedMusic.style}</p>
                <p><span className="font-medium">时长：</span>{generatedMusic.duration}秒</p>
                {generatedMusic.url && (
                  <audio controls className="w-full mt-4">
                    <source src={generatedMusic.url} type="audio/mpeg" />
                    您的浏览器不支持音频播放。
                  </audio>
                )}
              </div>
            </div>
          )}
        </div>

        <div className="mt-12">
          <h2 className="text-2xl font-semibold mb-6">音乐列表</h2>
          <MusicList />
        </div>
      </div>
    </main>
  );
} 