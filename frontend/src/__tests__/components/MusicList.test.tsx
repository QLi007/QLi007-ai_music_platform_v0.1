import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { MusicList } from '../../components/MusicList';
import { MusicService } from '../../services/musicService';

// Mock musicService
jest.mock('../../services/musicService', () => ({
  MusicService: {
    getMusicList: jest.fn(),
  },
}));

describe('MusicList', () => {
  const mockMusicList = {
    items: [
      {
        id: '1',
        title: 'Test Music 1',
        style: 'pop',
        duration: 60,
        url: 'test1.mp3',
        createdAt: '2024-01-22T10:00:00',
        status: 'COMPLETED'
      },
      {
        id: '2',
        title: 'Test Music 2',
        style: 'jazz',
        duration: 120,
        url: 'test2.mp3',
        createdAt: '2024-01-22T11:00:00',
        status: 'COMPLETED'
      }
    ],
    total: 3
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('应该渲染音乐列表', async () => {
    (MusicService.getMusicList as jest.Mock).mockResolvedValue(mockMusicList);

    await act(async () => {
      render(<MusicList />);
    });
    
    await waitFor(() => {
      expect(screen.getByText('Test Music 1')).toBeInTheDocument();
      expect(screen.getByText('Test Music 2')).toBeInTheDocument();
    });
  });

  it('应该显示音乐详情', async () => {
    (MusicService.getMusicList as jest.Mock).mockResolvedValue(mockMusicList);

    await act(async () => {
      render(<MusicList />);
    });
    
    await waitFor(() => {
      expect(screen.getByText('流行')).toBeInTheDocument();
      expect(screen.getByText('爵士')).toBeInTheDocument();
      expect(screen.getByText('60秒')).toBeInTheDocument();
      expect(screen.getByText('120秒')).toBeInTheDocument();
    });
  });

  it('应该处理空列表', async () => {
    (MusicService.getMusicList as jest.Mock).mockResolvedValueOnce({ items: [], total: 0 });
    
    await act(async () => {
      render(<MusicList />);
    });
    
    await waitFor(() => {
      expect(screen.getByText('暂无音乐')).toBeInTheDocument();
    });
  });

  it('应该处理加载状态', async () => {
    (MusicService.getMusicList as jest.Mock).mockImplementation(() => new Promise(() => {}));
    
    render(<MusicList />);
    expect(screen.getByText('加载中...')).toBeInTheDocument();
  });

  it('应该处理错误状态', async () => {
    const error = new Error('加载失败');
    (MusicService.getMusicList as jest.Mock).mockRejectedValueOnce(error);
    
    await act(async () => {
      render(<MusicList />);
    });
    
    await waitFor(() => {
      expect(screen.getByText('加载失败')).toBeInTheDocument();
    });
  });

  it('应该支持分页', async () => {
    const page2MusicList = {
      items: [
        {
          id: '3',
          title: 'Test Music 3',
          style: 'rock',
          duration: 180,
          url: 'test3.mp3',
          createdAt: '2024-01-22T12:00:00',
          status: 'COMPLETED'
        }
      ],
      total: 3
    };

    (MusicService.getMusicList as jest.Mock)
      .mockResolvedValueOnce(mockMusicList)
      .mockResolvedValueOnce(page2MusicList);

    await act(async () => {
      render(<MusicList />);
    });

    await waitFor(() => {
      expect(MusicService.getMusicList).toHaveBeenCalledWith(1);
    });

    const nextButton = screen.getByRole('button', { name: /下一页/i });
    expect(nextButton).not.toBeDisabled();
    
    await act(async () => {
      fireEvent.click(nextButton);
    });

    await waitFor(() => {
      expect(MusicService.getMusicList).toHaveBeenCalledWith(2);
      expect(screen.getByText('Test Music 3')).toBeInTheDocument();
    });
  });
}); 