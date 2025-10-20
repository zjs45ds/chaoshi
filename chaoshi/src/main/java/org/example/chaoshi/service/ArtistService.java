package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.dto.response.AlbumResponse;
import org.example.chaoshi.dto.response.SongResponse;
import org.example.chaoshi.entity.Artist;

import java.util.List;
import java.util.Map;

/**
 * 歌手Service接口
 */
public interface ArtistService {

    /**
     * 根据ID获取歌手
     */
    Artist getArtistById(Long id);

    /**
     * 分页查询歌手
     */
    PageResult<Artist> getArtistPage(String name, Integer page, Integer size);

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