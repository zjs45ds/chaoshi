package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Album;

import java.util.List;

/**
 * 专辑Service接口
 */
public interface AlbumService {
    
    /**
     * 创建专辑
     */
    Album createAlbum(Album album);
    
    /**
     * 根据ID获取专辑
     */
    Album getAlbumById(Long id);
    
    /**
     * 更新专辑信息
     */
    Album updateAlbum(Long id, Album album);
    
    /**
     * 删除专辑
     */
    boolean deleteAlbum(Long id);
    
    /**
     * 批量删除专辑
     */
    boolean deleteAlbums(List<Long> ids);
    
    /**
     * 分页查询专辑
     */
    PageResult<Album> getAlbumPage(String name, Long artistId, Integer releaseYear, Integer page, Integer size);
    
    /**
     * 根据歌手ID获取专辑列表
     */
    List<Album> getAlbumsByArtistId(Long artistId);
    
    /**
     * 搜索专辑
     */
    PageResult<Album> searchAlbums(String keyword, Integer page, Integer size);
    
    /**
     * 获取最新专辑
     */
    List<Album> getLatestAlbums(Integer limit);
    
    /**
     * 收藏/取消收藏专辑
     * @param userId 用户ID
     * @param albumId 专辑ID
     * @param action 操作类型：like/unlike
     * @return 是否收藏
     */
    boolean favoriteAlbum(Long userId, Long albumId, String action);
    
    /**
     * 检查用户是否收藏了指定专辑
     * @param userId 用户ID
     * @param albumId 专辑ID
     * @return 是否收藏
     */
    boolean isAlbumFavorited(Long userId, Long albumId);
    
    /**
     * 获取用户收藏的专辑列表
     * @param userId 用户ID
     * @return 用户收藏的专辑列表
     */
    List<Album> getUserFavoriteAlbums(Long userId);
}