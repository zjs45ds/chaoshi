package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Album;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 专辑Service接口
 */
public interface AlbumService {
    
    /**
     * 创建专辑
     */
    Album createAlbum(Album album, MultipartFile coverFile);
    
    /**
     * 根据ID获取专辑
     */
    Album getAlbumById(Long id);
    
    /**
     * 更新专辑信息
     */
    Album updateAlbum(Long id, Album album, MultipartFile coverFile);
    
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
}