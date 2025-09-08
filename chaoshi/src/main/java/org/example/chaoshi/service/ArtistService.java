package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.dto.response.AlbumResponse;
import org.example.chaoshi.dto.response.SongResponse;
import org.example.chaoshi.entity.Artist;
import org.example.chaoshi.entity.ArtistTimeline;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 歌手Service接口
 */
public interface ArtistService {
    
    /**
     * 创建歌手
     */
    Artist createArtist(Artist artist, MultipartFile avatarFile);
    
    /**
     * 根据ID获取歌手
     */
    Artist getArtistById(Long id);
    
    /**
     * 根据名称获取歌手
     */
    Artist getArtistByName(String name);
    
    /**
     * 更新歌手信息
     */
    Artist updateArtist(Long id, Artist artist, MultipartFile avatarFile);
    
    /**
     * 删除歌手
     */
    boolean deleteArtist(Long id);
    
    /**
     * 批量删除歌手
     */
    boolean deleteArtists(List<Long> ids);
    
    /**
     * 分页查询歌手
     */
    PageResult<Artist> getArtistPage(String name, Integer page, Integer size);
    
    /**
     * 搜索歌手
     */
    PageResult<Artist> searchArtists(String keyword, Integer page, Integer size);
    
    /**
     * 获取热门歌手
     */
    List<Artist> getPopularArtists(Integer limit);
    
    /**
     * 获取薛之谦专辑数据
     * @return 专辑列表
     */
    List<AlbumResponse> getXueZhiQianAlbums();
    
    /**
     * 获取薛之谦时间线
     * @return 时间线事件列表
     */
    List<ArtistTimeline> getXueZhiQianTimeline();
    
    /**
     * 获取薛之谦热门歌曲
     * @return 歌曲列表
     */
    List<SongResponse> getXueZhiQianHotSongs();
    
    /**
     * 获取薛之谦实时统计数据
     * @return 统计数据
     */
    Map<String, Object> getXueZhiQianStats();
}