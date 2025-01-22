import { create } from 'zustand';
import type { Music } from '../types/music';
import { MusicService } from '../services/musicService';

interface MusicStore {
  currentMusic: Music | null;
  currentIndex: number;
  playlist: Music[];
  musicList: Music[];
  isLoading: boolean;
  error: string | null;
  totalPages: number;
  currentPage: number;
  
  // Actions
  addToPlaylist: (music: Music) => void;
  playNextMusic: () => void;
  playPreviousMusic: () => void;
  generateMusic: (request: any) => Promise<void>;
  fetchMusicList: (page: number) => Promise<void>;
  getMusicById: (id: string) => Promise<void>;
}

export const useMusicStore = create<MusicStore>((set, get) => ({
  currentMusic: null,
  currentIndex: -1,
  playlist: [],
  musicList: [],
  isLoading: false,
  error: null,
  totalPages: 0,
  currentPage: 0,

  addToPlaylist: (music: Music) => {
    set(state => {
      const existingIndex = state.playlist.findIndex(m => m.id === music.id);
      if (existingIndex === -1) {
        const newPlaylist = [...state.playlist, music];
        return {
          ...state,
          playlist: newPlaylist,
          currentMusic: state.currentMusic || music,
          currentIndex: state.currentMusic ? state.currentIndex : newPlaylist.length - 1
        };
      }
      return state;
    });
  },

  playNextMusic: () => {
    const { playlist, currentIndex } = get();
    if (playlist.length === 0) return;

    const nextIndex = currentIndex === playlist.length - 1 ? 0 : currentIndex + 1;
    const nextMusic = playlist[nextIndex];
    
    set({
      currentIndex: nextIndex,
      currentMusic: nextMusic
    });
  },

  playPreviousMusic: () => {
    const { playlist, currentIndex } = get();
    if (playlist.length === 0) return;

    const prevIndex = currentIndex <= 0 ? playlist.length - 1 : currentIndex - 1;
    const prevMusic = playlist[prevIndex];
    
    set({
      currentIndex: prevIndex,
      currentMusic: prevMusic
    });
  },

  generateMusic: async (request: any) => {
    set({ isLoading: true, error: null });
    try {
      const music = await MusicService.generateMusic(request);
      set((state) => ({
        musicList: [music, ...state.musicList],
        currentMusic: music,
        isLoading: false
      }));
    } catch (error) {
      set({ error: '生成音乐失败', isLoading: false });
    }
  },

  fetchMusicList: async (page: number) => {
    set({ isLoading: true, error: null });
    try {
      const { items, total } = await MusicService.getMusicList(page);
      set({
        musicList: items,
        totalPages: Math.ceil(total / 10),
        currentPage: page,
        isLoading: false
      });
    } catch (error) {
      set({ error: '获取音乐列表失败', isLoading: false });
    }
  },

  getMusicById: async (id: string) => {
    set({ isLoading: true, error: null });
    try {
      const music = await MusicService.getMusicById(id);
      set({ currentMusic: music, isLoading: false });
    } catch (error) {
      set({ error: '获取音乐详情失败', isLoading: false });
    }
  }
})); 