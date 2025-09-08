package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.dto.response.SongResponse;
import org.example.chaoshi.entity.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 歌曲Service接口
 */
public interface SongService {
    
    /**
     * 创建歌曲
     */
    Song createSong(Song song, MultipartFile audioFile, MultipartFile coverFile);
    
    /**
     * 根据ID获取歌曲
     */
    Song getSongById(Long id);
    
    /**
     * 更新歌曲信息
     */
    Song updateSong(Long id, Song song, MultipartFile audioFile, MultipartFile coverFile);
    
    /**
     * 删除歌曲
     */
    boolean deleteSong(Long id);
    
    /**
     * 批量删除歌曲
     */
    boolean deleteSongs(List<Long> ids);
    
    /**
     * 分页查询歌曲
     */
    PageResult<Song> getSongPage(String name, Long artistId, Long albumId, Integer page, Integer size);
    
    /**
     * 根据歌手ID获取歌曲列表
     */
    List<Song> getSongsByArtistId(Long artistId);
    
    /**
     * 根据专辑ID获取歌曲列表
     */
    List<Song> getSongsByAlbumId(Long albumId);
    
    /**
     * 搜索歌曲
     */
    PageResult<Song> searchSongs(String keyword, Integer page, Integer size);
    
    /**
     * 分页获取歌曲列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页歌曲列表
     */
    PageResult<SongResponse> getSongList(Integer page, Integer size);
    
    /**
     * 收藏/取消收藏歌曲
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @param action 操作类型：like/unlike
     * @return 是否收藏
     */
    boolean favoriteSong(Long userId, Long songId, String action);
    
    /**
     * 获取用户收藏的歌曲列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 用户收藏的歌曲分页列表
     */
    PageResult<SongResponse> getUserFavoriteSongs(Long userId, Integer page, Integer size);
    
    /**
     * 检查用户是否收藏了指定歌曲
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @return 是否收藏
     */
    boolean isSongFavorited(Long userId, Long songId);
    
    /**
     * 获取歌曲流媒体URL
     * @param songId 歌曲ID
     * @return 包含音频URL和相关信息的Map
     */
    java.util.Map<String, Object> getSongStreamUrl(Long songId);
}