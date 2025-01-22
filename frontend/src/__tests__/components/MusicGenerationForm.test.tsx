import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MusicGenerationForm } from '../../components/MusicGenerationForm';
import { MusicService } from '../../services/musicService';

// Mock musicService
jest.mock('../../services/musicService', () => ({
  MusicService: {
    generateMusic: jest.fn(),
  },
}));

describe('MusicGenerationForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('应该渲染所有必要的表单字段', () => {
    render(<MusicGenerationForm onSuccess={() => {}} onError={() => {}} />);
    
    expect(screen.getByLabelText(/风格/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/时长/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/标题/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /生成/i })).toBeInTheDocument();
  });

  it('应该在提交时调用generateMusic', async () => {
    const mockSuccess = jest.fn();
    const mockMusic = { id: '1', title: 'Test Music', style: 'pop', duration: 60, url: 'test.mp3', createdAt: '2024-01-22' };
    (MusicService.generateMusic as jest.Mock).mockResolvedValueOnce(mockMusic);

    render(<MusicGenerationForm onSuccess={mockSuccess} onError={() => {}} />);
    
    fireEvent.change(screen.getByLabelText(/风格/i), { target: { value: 'pop' } });
    fireEvent.change(screen.getByLabelText(/时长/i), { target: { value: '60' } });
    fireEvent.change(screen.getByLabelText(/标题/i), { target: { value: 'Test Music' } });
    
    fireEvent.click(screen.getByRole('button', { name: /生成/i }));
    
    await waitFor(() => {
      expect(MusicService.generateMusic).toHaveBeenCalledWith({
        style: 'pop',
        duration: 60,
        title: 'Test Music'
      });
      expect(mockSuccess).toHaveBeenCalledWith(mockMusic);
    });
  });

  it('应该在生成失败时调用onError', async () => {
    const mockError = jest.fn();
    const error = new Error('生成失败');
    (MusicService.generateMusic as jest.Mock).mockRejectedValueOnce(error);

    render(<MusicGenerationForm onSuccess={() => {}} onError={mockError} />);
    
    fireEvent.change(screen.getByLabelText(/风格/i), { target: { value: 'pop' } });
    fireEvent.change(screen.getByLabelText(/时长/i), { target: { value: '60' } });
    
    fireEvent.click(screen.getByRole('button', { name: /生成/i }));
    
    await waitFor(() => {
      expect(mockError).toHaveBeenCalledWith(error);
    });
  });
}); 