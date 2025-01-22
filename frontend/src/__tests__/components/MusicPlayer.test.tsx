import React from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { MusicPlayer } from '@/components/MusicPlayer';

describe('MusicPlayer Component', () => {
  const mockMusic = {
    id: '1',
    title: '测试音乐',
    url: 'http://example.com/music.mp3'
  };

  beforeEach(() => {
    // Mock HTMLMediaElement
    window.HTMLMediaElement.prototype.play = jest.fn().mockResolvedValue(undefined);
    window.HTMLMediaElement.prototype.pause = jest.fn().mockResolvedValue(undefined);
    window.HTMLMediaElement.prototype.load = jest.fn();
  });

  it('应该正确渲染音乐播放器', () => {
    render(<MusicPlayer music={mockMusic} />);
    
    expect(screen.getByText('测试音乐')).toBeInTheDocument();
    expect(screen.getByLabelText('进度条')).toBeInTheDocument();
    expect(screen.getByLabelText('音量')).toBeInTheDocument();
  });

  it('应该显示加载中状态', () => {
    render(<MusicPlayer music={mockMusic} />);
    
    const loadingElement = screen.getByText('加载中...');
    expect(loadingElement).toBeInTheDocument();
  });

  it('应该处理播放/暂停', async () => {
    render(<MusicPlayer music={mockMusic} />);
    
    // 模拟音频加载完成
    const audio = screen.getByTestId('audio-element');
    fireEvent.loadedData(audio);
    
    // 点击播放
    const playButton = screen.getByLabelText('播放');
    await act(async () => {
      fireEvent.click(playButton);
    });
    expect(window.HTMLMediaElement.prototype.play).toHaveBeenCalled();
    
    // 点击暂停
    const pauseButton = screen.getByLabelText('暂停');
    await act(async () => {
      fireEvent.click(pauseButton);
    });
    expect(window.HTMLMediaElement.prototype.pause).toHaveBeenCalled();
  });

  it('应该处理音量调节', () => {
    render(<MusicPlayer music={mockMusic} />);
    
    const volumeSlider = screen.getByLabelText('音量');
    fireEvent.change(volumeSlider, { target: { value: '0.5' } });
    
    expect(volumeSlider).toHaveValue('0.5');
  });

  it('应该处理无效的音乐URL', () => {
    render(<MusicPlayer music={{ ...mockMusic, url: null }} />);
    
    expect(screen.getByText('无法播放音乐')).toBeInTheDocument();
  });
}); 