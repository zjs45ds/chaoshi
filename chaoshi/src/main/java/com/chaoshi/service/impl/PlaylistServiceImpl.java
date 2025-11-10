package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Playlist;
import com.chaoshi.entity.PlaylistSong;
import com.chaoshi.entity.Song;
import com.chaoshi.mapper.PlaylistMapper;
import com.chaoshi.mapper.PlaylistSongMapper;
import com.chaoshi.mapper.SongMapper;
import com.chaoshi.service.PlaylistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    
    private final PlaylistMapper playlistMapper;
    private final SongMapper songMapper;
    private final PlaylistSongMapper playlistSongMapper;


    @Override
    public Playlist getPlaylistById(Long id) {
        try {
            Playlist playlist = playlistMapper.selectById(id);
            if (playlist == null) {
                throw new RuntimeException("播放列表不存在");
            }
            return playlist;
        } catch (Exception e) {
            log.error("获取播放列表失败: {}", id, e);
            throw new RuntimeException("获取播放列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Playlist updatePlaylist(Long id, Playlist playlist) {
        try {
            Playlist existingPlaylist = getPlaylistById(id);
            if (existingPlaylist == null) {
                throw new RuntimeException("播放列表不存在");
            }
            
            playlist.setId(id);
            playlist.setUpdatedAt(LocalDateTime.now());
            
            int result = playlistMapper.updatePlaylist(playlist);
            if (result > 0) {
                log.info("播放列表更新成功: {}", id);
                return getPlaylistById(id);
            } else {
                throw new RuntimeException("播放列表更新失败");
            }
        } catch (Exception e) {
            log.error("更新播放列表失败: {}", id, e);
            throw new RuntimeException("更新播放列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deletePlaylist(Long id) {
        try {
            int result = playlistMapper.deletePlaylist(id);
            log.info("播放列表删除成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除播放列表失败: {}", id, e);
            throw new RuntimeException("删除播放列表失败: " + e.getMessage());
        }
    }

    public PageResult<Playlist> getPlaylistPage(String name, Long userId, Boolean isPublic, Integer page, Integer size) {
        try {
            List<Playlist> playlists = playlistMapper.selectAll();
            return new PageResult<>((long)playlists.size(), playlists, page, size);
        } catch (Exception e) {
            log.error("分页获取播放列表失败", e);
            throw new RuntimeException("分页获取播放列表失败: " + e.getMessage());
        }
    }

    public PageResult<Playlist> searchPlaylists(String keyword, Integer page, Integer size) {
        return getPlaylistPage(keyword, null, null, page, size);
    }

    public boolean addSongToPlaylist(Long playlistId, Long songId) {

        return true;
    }

    public boolean removeSongFromPlaylist(Long playlistId, Long songId) {

        return true;
    }

    @Override
    public List<Playlist> getPlaylistsByUserId(Long userId) {
        try {
            return playlistMapper.selectByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户播放列表失败: {}", userId, e);
            throw new RuntimeException("获取用户播放列表失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Playlist> getPublicPlaylists(Integer page, Integer size) {
        try {
            // 计算偏移量
            Long offset = (long)(page - 1) * size;
            // 获取公开播放列表
            List<Playlist> playlists = playlistMapper.selectPublicPlaylists(offset, (long)size);
            // 获取总数量
            Long total = playlistMapper.countPlaylists(null, null, true);
            
            return new PageResult<>(total, playlists, page, size);
        } catch (Exception e) {
            log.error("获取公开播放列表失败", e);
            throw new RuntimeException("获取公开播放列表失败: " + e.getMessage());
        }
    }

    @Override
    public List<Song> getPlaylistSongs(Long playlistId) {
        try {
            // 检查歌单是否存在
            getPlaylistById(playlistId);
            
            // 获取歌单中的歌曲关联信息
            List<PlaylistSong> playlistSongs = playlistSongMapper.selectByPlaylistId(playlistId);
            
            if (playlistSongs == null || playlistSongs.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 获取每首歌曲的完整信息（带歌手和专辑信息）
            List<Song> songs = new ArrayList<>();
            for (PlaylistSong playlistSong : playlistSongs) {
                // 使用带歌手和专辑信息的查询方法
                Song song = songMapper.selectByIdWithArtistAndAlbum(playlistSong.getSongId());
                if (song != null) {
                    // 如果歌手名或专辑名为空，设置默认值
                    if (song.getArtist() == null) {
                        song.setArtist("未知歌手");
                    }
                    if (song.getAlbum() == null) {
                        song.setAlbum("未知专辑");
                    }
                    // 如果时长为null或0，设置默认值
                    if (song.getDuration() == null || song.getDuration() <= 0) {
                        song.setDuration(180); // 默认3分钟
                    }
                    songs.add(song);
                }
            }
            
            return songs;
        } catch (Exception e) {
            log.error("获取歌单歌曲失败: {}", playlistId, e);
            throw new RuntimeException("获取歌单歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public boolean updateSongOrder(Long playlistId, List<Long> songIds) {

        return true;
    }

    @Override
    public boolean clearPlaylist(Long playlistId) {

        return true;
    }
}
