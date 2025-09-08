package org.example.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Playlist;
import org.example.chaoshi.entity.PlaylistSong;
import org.example.chaoshi.entity.Song;
import org.example.chaoshi.mapper.PlaylistMapper;
import org.example.chaoshi.mapper.PlaylistSongMapper;
import org.example.chaoshi.mapper.SongMapper;
import org.example.chaoshi.service.FileUploadService;
import org.example.chaoshi.service.PlaylistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 播放列表Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    
    private final PlaylistMapper playlistMapper;
    private final PlaylistSongMapper playlistSongMapper;
    private final SongMapper songMapper;
    private final FileUploadService fileUploadService;
    
    @Override
    @Transactional
    public Playlist createPlaylist(Playlist playlist, MultipartFile coverFile) {
        try {
            // 上传封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                String coverUrl = fileUploadService.uploadFile(coverFile, "image");
                playlist.setCoverUrl(coverUrl);
            }
            
            playlist.setCreatedAt(LocalDateTime.now());
            playlist.setUpdatedAt(LocalDateTime.now());
            playlist.setSongCount(0);
            playlist.setPlayCount(0L);
            
            playlistMapper.insertPlaylist(playlist);
            
            log.info("播放列表创建成功: {}", playlist.getId());
            return playlist;
        } catch (Exception e) {
            log.error("创建播放列表失败", e);
            throw new RuntimeException("创建播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Playlist getPlaylistById(Long id) {
        Playlist playlist = playlistMapper.selectById(id);
        if (playlist == null) {
            throw new RuntimeException("播放列表不存在: " + id);
        }
        return playlist;
    }
    
    @Override
    @Transactional
    public Playlist updatePlaylist(Long id, Playlist playlist, MultipartFile coverFile) {
        try {
            Playlist existingPlaylist = getPlaylistById(id);
            
            // 上传新封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                // 删除旧封面
                if (existingPlaylist.getCoverUrl() != null) {
                    fileUploadService.deleteFile(existingPlaylist.getCoverUrl());
                }
                String coverUrl = fileUploadService.uploadFile(coverFile, "image");
                playlist.setCoverUrl(coverUrl);
            }
            
            playlist.setId(id);
            playlist.setUpdatedAt(LocalDateTime.now());
            playlistMapper.updatePlaylist(playlist);
            
            log.info("播放列表更新成功: {}", id);
            return getPlaylistById(id);
        } catch (Exception e) {
            log.error("更新播放列表失败: {}", id, e);
            throw new RuntimeException("更新播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deletePlaylist(Long id) {
        try {
            Playlist playlist = getPlaylistById(id);
            
            // 删除封面文件
            if (playlist.getCoverUrl() != null) {
                fileUploadService.deleteFile(playlist.getCoverUrl());
            }
            
            // 清空播放列表中的歌曲
            playlistSongMapper.clearPlaylist(id);
            
            // 删除播放列表
            int result = playlistMapper.deletePlaylist(id);
            
            log.info("播放列表删除成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除播放列表失败: {}", id, e);
            throw new RuntimeException("删除播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Playlist> getPlaylistPage(String name, Long userId, Boolean isPublic, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Playlist> playlists = playlistMapper.selectPage(name, userId, isPublic, offset, (long) size);
            Long total = playlistMapper.countPlaylists(name, userId, isPublic);
            
            return new PageResult<>(total, playlists, page, size);
        } catch (Exception e) {
            log.error("分页查询播放列表失败", e);
            throw new RuntimeException("分页查询播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Playlist> getPlaylistsByUserId(Long userId) {
        try {
            return playlistMapper.selectByUserId(userId);
        } catch (Exception e) {
            log.error("根据用户ID查询播放列表失败: {}", userId, e);
            throw new RuntimeException("根据用户ID查询播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Playlist> getPublicPlaylists(Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Playlist> playlists = playlistMapper.selectPublicPlaylists(offset, (long) size);
            Long total = playlistMapper.countPlaylists(null, null, true);
            
            return new PageResult<>(total, playlists, page, size);
        } catch (Exception e) {
            log.error("查询公开播放列表失败", e);
            throw new RuntimeException("查询公开播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Playlist> searchPlaylists(String keyword, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Playlist> playlists = playlistMapper.searchPlaylists(keyword, offset, (long) size);
            // 搜索结果的总数需要单独查询，这里暂时使用查询结果的数量
            Long total = (long) playlists.size();
            
            return new PageResult<>(total, playlists, page, size);
        } catch (Exception e) {
            log.error("搜索播放列表失败", e);
            throw new RuntimeException("搜索播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean addSongToPlaylist(Long playlistId, Long songId) {
        try {
            // 检查播放列表是否存在
            getPlaylistById(playlistId);
            
            // 检查歌曲是否存在
            Song song = songMapper.selectById(songId);
            if (song == null) {
                throw new RuntimeException("歌曲不存在: " + songId);
            }
            
            // 检查歌曲是否已在播放列表中
            Boolean exists = playlistSongMapper.existsInPlaylist(playlistId, songId);
            if (Boolean.TRUE.equals(exists)) {
                throw new RuntimeException("歌曲已在播放列表中");
            }
            
            // 获取最大排序号
            Integer maxSortOrder = playlistSongMapper.getMaxSortOrder(playlistId);
            int sortOrder = (maxSortOrder != null) ? maxSortOrder + 1 : 1;
            
            // 添加歌曲到播放列表
            PlaylistSong playlistSong = new PlaylistSong();
            playlistSong.setPlaylistId(playlistId);
            playlistSong.setSongId(songId);
            playlistSong.setSortOrder(sortOrder);
            playlistSong.setCreatedAt(LocalDateTime.now());
            
            int result = playlistSongMapper.insertPlaylistSong(playlistSong);
            
            log.info("歌曲添加到播放列表成功: playlistId={}, songId={}", playlistId, songId);
            return result > 0;
        } catch (Exception e) {
            log.error("添加歌曲到播放列表失败: playlistId={}, songId={}", playlistId, songId, e);
            throw new RuntimeException("添加歌曲到播放列表失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean removeSongFromPlaylist(Long playlistId, Long songId) {
        try {
            // 检查播放列表是否存在
            getPlaylistById(playlistId);
            
            int result = playlistSongMapper.removeFromPlaylist(playlistId, songId);
            
            log.info("从播放列表移除歌曲成功: playlistId={}, songId={}", playlistId, songId);
            return result > 0;
        } catch (Exception e) {
            log.error("从播放列表移除歌曲失败: playlistId={}, songId={}", playlistId, songId, e);
            throw new RuntimeException("从播放列表移除歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Song> getPlaylistSongs(Long playlistId) {
        try {
            // 检查播放列表是否存在
            getPlaylistById(playlistId);
            
            // 获取播放列表中的歌曲关联
            List<PlaylistSong> playlistSongs = playlistSongMapper.selectByPlaylistId(playlistId);
            
            // 获取歌曲详情
            List<Song> songs = new ArrayList<>();
            for (PlaylistSong playlistSong : playlistSongs) {
                Song song = songMapper.selectById(playlistSong.getSongId());
                if (song != null) {
                    songs.add(song);
                }
            }
            
            return songs;
        } catch (Exception e) {
            log.error("获取播放列表歌曲失败: playlistId={}", playlistId, e);
            throw new RuntimeException("获取播放列表歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateSongOrder(Long playlistId, List<Long> songIds) {
        try {
            // 检查播放列表是否存在
            getPlaylistById(playlistId);
            
            List<PlaylistSong> playlistSongs = new ArrayList<>();
            for (int i = 0; i < songIds.size(); i++) {
                PlaylistSong playlistSong = new PlaylistSong();
                playlistSong.setPlaylistId(playlistId);
                playlistSong.setSongId(songIds.get(i));
                playlistSong.setSortOrder(i + 1);
                playlistSongs.add(playlistSong);
            }
            
            int result = playlistSongMapper.batchUpdateSortOrder(playlistSongs);
            
            log.info("更新播放列表歌曲排序成功: playlistId={}", playlistId);
            return result > 0;
        } catch (Exception e) {
            log.error("更新播放列表歌曲排序失败: playlistId={}", playlistId, e);
            throw new RuntimeException("更新播放列表歌曲排序失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean clearPlaylist(Long playlistId) {
        try {
            // 检查播放列表是否存在
            getPlaylistById(playlistId);
            
            int result = playlistSongMapper.clearPlaylist(playlistId);
            
            log.info("清空播放列表成功: playlistId={}", playlistId);
            return result > 0;
        } catch (Exception e) {
            log.error("清空播放列表失败: playlistId={}", playlistId, e);
            throw new RuntimeException("清空播放列表失败: " + e.getMessage());
        }
    }
}