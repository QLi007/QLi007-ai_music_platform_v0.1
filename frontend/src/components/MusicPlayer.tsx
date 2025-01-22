'use client';

import React, { useEffect, useRef, useState } from 'react';
import { PlayIcon, PauseIcon, SpeakerWaveIcon, SpeakerXMarkIcon } from '@heroicons/react/24/solid';

interface MusicPlayerProps {
  music: {
    id: string;
    title: string;
    url: string | null;
  };
}

export function MusicPlayer({ music }: MusicPlayerProps) {
  const audioRef = useRef<HTMLAudioElement>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(1);
  const [isMuted, setIsMuted] = useState(false);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.volume = volume;
    }
  }, [volume]);

  useEffect(() => {
    setIsPlaying(false);
    setIsLoading(true);
    setCurrentTime(0);
    setDuration(0);
  }, [music.url]);

  const handlePlay = async () => {
    if (audioRef.current) {
      try {
        if (isPlaying) {
          await audioRef.current.pause();
        } else {
          await audioRef.current.play();
        }
        setIsPlaying(!isPlaying);
      } catch (error) {
        console.error('播放出错:', error);
      }
    }
  };

  const handleTimeUpdate = () => {
    if (audioRef.current) {
      setCurrentTime(audioRef.current.currentTime);
    }
  };

  const handleLoadedData = () => {
    if (audioRef.current) {
      setDuration(audioRef.current.duration);
      setIsLoading(false);
    }
  };

  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const time = parseFloat(e.target.value);
    if (audioRef.current) {
      audioRef.current.currentTime = time;
      setCurrentTime(time);
    }
  };

  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = parseFloat(e.target.value);
    setVolume(value);
    setIsMuted(value === 0);
  };

  const toggleMute = () => {
    if (audioRef.current) {
      if (isMuted) {
        setVolume(1);
        setIsMuted(false);
      } else {
        setVolume(0);
        setIsMuted(true);
      }
    }
  };

  const formatTime = (time: number) => {
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  if (!music.url) {
    return (
      <div className="text-center p-4 text-red-600">
        无法播放音乐
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold" data-testid="player-title">{music.title}</h3>
        <div className="flex items-center space-x-2">
          <button
            onClick={toggleMute}
            className="p-2 hover:bg-gray-100 rounded-full"
            aria-label={isMuted ? '取消静音' : '静音'}
          >
            {isMuted ? (
              <SpeakerXMarkIcon className="h-5 w-5 text-gray-600" />
            ) : (
              <SpeakerWaveIcon className="h-5 w-5 text-gray-600" />
            )}
          </button>
          <input
            type="range"
            min="0"
            max="1"
            step="0.1"
            value={volume}
            onChange={handleVolumeChange}
            className="w-24"
            aria-label="音量"
          />
        </div>
      </div>

      <div className="space-y-2">
        <div className="flex items-center space-x-4">
          <button
            onClick={handlePlay}
            disabled={isLoading}
            className="p-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label={isPlaying ? '暂停' : '播放'}
          >
            {isPlaying ? (
              <PauseIcon className="h-6 w-6" />
            ) : (
              <PlayIcon className="h-6 w-6" />
            )}
          </button>
          <div className="flex-1 flex items-center space-x-2">
            <span className="text-sm text-gray-600 w-12">
              {formatTime(currentTime)}
            </span>
            <input
              type="range"
              min="0"
              max={duration || 0}
              value={currentTime}
              onChange={handleSeek}
              className="flex-1"
              disabled={isLoading}
              aria-label="进度条"
            />
            <span className="text-sm text-gray-600 w-12">
              {formatTime(duration)}
            </span>
          </div>
        </div>

        {isLoading && (
          <div className="text-center text-gray-600">
            加载中...
          </div>
        )}
      </div>

      <audio
        ref={audioRef}
        src={music.url}
        onTimeUpdate={handleTimeUpdate}
        onLoadedData={handleLoadedData}
        onEnded={() => setIsPlaying(false)}
        data-testid="audio-element"
      />
    </div>
  );
} 