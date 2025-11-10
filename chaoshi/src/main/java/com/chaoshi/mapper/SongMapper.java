package com.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.chaoshi.entity.Song;

import java.util.List;

/**
 * 歌曲Mapper接口
 */
@Mapper
public interface SongMapper {
    
    /**
     * 根据ID查询歌曲
     */
    Song selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询歌曲(带歌手和专辑信息)
     */
    Song selectByIdWithArtistAndAlbum(@Param("id") Long id);
    
    /**
     * 查询所有歌曲
     */
    List<Song> selectAll();
    
    /**
     * 插入歌曲
     */
    int insertSong(Song song);
    
    /**
     * 批量插入歌曲
     */
    int insertBatch(@Param("list") List<Song> songs);
    
    /**
     * 根据专辑ID查询歌曲列表
     */
    List<Song> selectByAlbumId(@Param("albumId") Long albumId);
    
    /**
     * 根据专辑ID查询歌曲列表(带歌手和专辑信息)
     */
    List<Song> selectByAlbumIdWithArtistAndAlbum(@Param("albumId") Long albumId);
    
    /**
     * 根据歌手ID查询歌曲列表
     */
    List<Song> selectByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 根据歌手ID查询歌曲列表(带歌手和专辑信息)
     */
    List<Song> selectByArtistIdWithArtistAndAlbum(@Param("artistId") Long artistId);
    
    /**
     * 分页查询歌曲
     */
    List<Song> selectPage(@Param("name") String name, 
                         @Param("artistId") Long artistId,
                         @Param("albumId") Long albumId,
                         @Param("offset") Long offset, 
                         @Param("size") Long size);
    
    /**
     * 统计歌曲数量
     */
    Long countSongs(@Param("name") String name, 
                    @Param("artistId") Long artistId,
                    @Param("albumId") Long albumId);
    
    /**
     * 更新歌曲信息
     */
    int updateSong(Song song);
    
    /**
     * 逻辑删除歌曲
     */
    int deleteSong(@Param("id") Long id);
    
    /**
     * 批量逻辑删除歌曲
     */
    int deleteBatch(@Param("list") List<Long> ids);
    
    /**
     * 搜索歌曲
     */
    List<Song> searchSongs(@Param("keyword") String keyword, 
                          @Param("offset") Long offset, 
                          @Param("size") Long size);
}