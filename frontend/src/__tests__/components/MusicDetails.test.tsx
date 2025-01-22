import React from 'react';
import { render, screen } from '@testing-library/react';
import { MusicDetails } from '@/components/MusicDetails';
import { MusicService } from '@/services/musicService';

// Mock MusicService
jest.mock('@/services/musicService');

describe('MusicDetails Component', () => {
  const mockMusic = {
    id: '1',
    title: '测试音乐',
    prompt: '测试提示词',
    style: 'pop',
    duration: 180,
    status: 'COMPLETED',
    url: 'http://example.com/music.mp3',
    createdAt: '2024-01-22T12:00:00Z',
    updatedAt: '2024-01-22T12:00:00Z'
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('应该正确渲染音乐详情', async () => {
    (MusicService.getMusicById as jest.Mock).mockResolvedValue(mockMusic);
    
    render(<MusicDetails musicId="1" />);
    
    // 检查加载状态
    expect(screen.getByText('加载中...')).toBeInTheDocument();
    
    // 等待数据加载
    expect(await screen.findByText('测试音乐')).toBeInTheDocument();
    expect(screen.getByText('流行')).toBeInTheDocument();
    expect(screen.getByText('180秒')).toBeInTheDocument();
    
    // 检查日期（使用更灵活的匹配方式）
    const dateRegex = /2024\/1\/22/;
    const dateElements = screen.getAllByText(dateRegex);
    expect(dateElements.length).toBeGreaterThan(0);
  });

  it('应该处理加载错误', async () => {
    const errorMessage = '加载失败';
    (MusicService.getMusicById as jest.Mock).mockRejectedValue(new Error(errorMessage));
    
    render(<MusicDetails musicId="1" />);
    
    expect(await screen.findByText(errorMessage)).toBeInTheDocument();
  });

  it('应该处理无效的音乐ID', async () => {
    render(<MusicDetails musicId="" />);
    
    expect(screen.getByText('无效的音乐ID')).toBeInTheDocument();
  });
}); 