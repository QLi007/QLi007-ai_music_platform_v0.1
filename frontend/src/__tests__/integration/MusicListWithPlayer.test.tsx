import React, { useState } from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { MusicList } from '../../components/MusicList';
import { MusicPlayer } from '../../components/MusicPlayer';
import { MusicService } from '../../services/musicService';
import type { Music } from '../../types/music';

// Mock musicService
jest.mock('../../services/musicService');

describe('Music List and Player Integration', () => {
  const mockMusicList: Music[] = [
    {
      id: '1',
      title: '测试音乐1',
      prompt: '测试提示1',
      style: '流行',
      duration: 180,
      status: 'COMPLETED',
      url: 'http://example.com/music1.mp3',
      createdAt: '2024/1/22 06:00:00',
      updatedAt: '2024/1/22 06:00:00'
    },
    {
      id: '2',
      title: '测试音乐2',
      prompt: '测试提示2',
      style: '爵士',
      duration: 240,
      status: 'COMPLETED',
      url: 'http://example.com/music2.mp3',
      createdAt: '2024/1/22 07:00:00',
      updatedAt: '2024/1/22 07:00:00'
    }
  ];

  const mockMusicListPage2: Music[] = [
    {
      id: '3',
      title: '测试音乐3',
      prompt: '测试提示3',
      style: '古典',
      duration: 300,
      status: 'COMPLETED',
      url: 'http://example.com/music3.mp3',
      createdAt: '2024/1/22 08:00:00',
      updatedAt: '2024/1/22 08:00:00'
    }
  ];

  beforeEach(() => {
    // Reset all mocks
    jest.clearAllMocks();
    
    // Mock the musicService.listMusic implementation
    (MusicService.getMusicList as jest.Mock).mockImplementation((page = 1) => {
      const mockData = page === 1 ? mockMusicList : mockMusicListPage2;
      return Promise.resolve({
        items: mockData,
        total: 3
      });
    });
  });

  it('应该正确处理音乐选择和播放', async () => {
    const MusicListWithPlayer: React.FC = () => {
      const [selectedMusic, setSelectedMusic] = useState<Music | null>(null);
      return (
        <div>
          <MusicList onMusicSelect={setSelectedMusic} />
          {selectedMusic && <MusicPlayer music={selectedMusic} />}
        </div>
      );
    };

    render(<MusicListWithPlayer />);

    // 等待音乐列表加载
    const musicList = await screen.findByTestId('music-list');
    expect(musicList).toBeInTheDocument();

    // 点击第一个音乐卡片
    const firstMusicCard = screen.getByTestId('music-card-1');
    fireEvent.click(firstMusicCard);

    // 验证播放器标题更新
    expect(screen.getByTestId('player-title')).toHaveTextContent('测试音乐1');

    // 点击第二个音乐卡片
    const secondMusicCard = screen.getByTestId('music-card-2');
    fireEvent.click(secondMusicCard);

    // 验证播放器标题更新
    expect(screen.getByTestId('player-title')).toHaveTextContent('测试音乐2');
  });

  it('应该正确处理分页', async () => {
    render(<MusicList />);

    // 等待音乐列表加载
    const musicList = await screen.findByTestId('music-list');
    expect(musicList).toBeInTheDocument();

    // 验证第一页数据
    expect(screen.getByTestId('music-card-1')).toBeInTheDocument();
    expect(screen.getByTestId('music-card-2')).toBeInTheDocument();

    // 点击下一页按钮
    const nextButton = screen.getByRole('button', { name: /下一页/i });
    expect(nextButton).not.toBeDisabled();
    await act(async () => {
      fireEvent.click(nextButton);
    });

    // 验证第二页数据
    const thirdMusicCard = await screen.findByTestId('music-card-3');
    expect(thirdMusicCard).toBeInTheDocument();
    expect(screen.queryByTestId('music-card-1')).not.toBeInTheDocument();
    expect(screen.queryByTestId('music-card-2')).not.toBeInTheDocument();

    // 验证分页按钮状态
    const prevButton = screen.getByRole('button', { name: /上一页/i });
    expect(prevButton).not.toBeDisabled();
    expect(nextButton).toBeDisabled();
  });
}); 