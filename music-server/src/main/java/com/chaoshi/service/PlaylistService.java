package com.chaoshi.service;

import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Playlist;
import com.chaoshi.entity.Song;

import java.util.List;

/**
 * 播放列表Service接口
 */
public interface PlaylistService {

    /**
     * 根据ID获取播放列表
     */
    Playlist getPlaylistById(Long id);
    
    /**
     * 更新播放列表信息
     */
    Playlist updatePlaylist(Long id, Playlist playlist);
    
    /**
     * 删除播放列表
     */
    boolean deletePlaylist(Long id);
    
    /**
     * 分页查询播放列表
     */
    PageResult<Playlist> getPlaylistPage(String name, Long userId, Boolean isPublic, Integer page, Integer size);
    
    /**
     * 根据用户ID获取播放列表
     */
    List<Playlist> getPlaylistsByUserId(Long userId);
    
    /**
     * 获取公开播放列表
     */
    PageResult<Playlist> getPublicPlaylists(Integer page, Integer size);
    
    /**
     * 搜索播放列表
     */
    PageResult<Playlist> searchPlaylists(String keyword, Integer page, Integer size);
    
    /**
     * 添加歌曲到播放列表
     */
    boolean addSongToPlaylist(Long playlistId, Long songId);
    
    /**
     * 从播放列表移除歌曲
     */
    boolean removeSongFromPlaylist(Long playlistId, Long songId);
    
    /**
     * 获取播放列表中的歌曲
     */
    List<Song> getPlaylistSongs(Long playlistId);
    
    /**
     * 更新播放列表中歌曲的排序
     */
    boolean updateSongOrder(Long playlistId, List<Long> songIds);
    
    /**
     * 清空播放列表
     */
    boolean clearPlaylist(Long playlistId);
}