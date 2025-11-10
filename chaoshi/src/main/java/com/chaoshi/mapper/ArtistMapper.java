package com.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.chaoshi.entity.Artist;

import java.util.List;

/**
 * 歌手Mapper接口
 */
@Mapper
public interface ArtistMapper {
    
    /**
     * 根据ID查询歌手
     */
    Artist selectById(@Param("id") Long id);
    
    /**
     * 查询所有歌手
     */
    List<Artist> selectAll();
    
    /**
     * 插入歌手
     */
    int insertArtist(Artist artist);
    
    /**
     * 批量插入歌手
     */
    int insertBatch(@Param("list") List<Artist> artists);
    
    /**
     * 根据名称查询歌手
     */
    Artist selectByName(@Param("name") String name);
    
    /**
     * 分页查询歌手
     */
    List<Artist> selectPage(@Param("name") String name,
                           @Param("offset") Long offset, 
                           @Param("size") Long size);
    
    /**
     * 统计歌手数量
     */
    Long countArtists(@Param("name") String name);
    
    /**
     * 更新歌手信息
     */
    int updateArtist(Artist artist);
    
    /**
     * 逻辑删除歌手
     */
    int deleteArtist(@Param("id") Long id);
    
    /**
     * 批量逻辑删除歌手
     */
    int deleteBatch(@Param("list") List<Long> ids);
    
    /**
     * 搜索歌手
     */
    List<Artist> searchArtists(@Param("keyword") String keyword, 
                              @Param("offset") Long offset, 
                              @Param("size") Long size);
    
    /**
     * 获取热门歌手
     */
    List<Artist> selectPopularArtists(@Param("limit") Integer limit);
}