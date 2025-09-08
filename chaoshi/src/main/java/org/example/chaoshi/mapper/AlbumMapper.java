package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.Album;

import java.util.List;

/**
 * 专辑Mapper接口
 */
@Mapper
public interface AlbumMapper {
    
    /**
     * 根据ID查询专辑
     */
    Album selectById(@Param("id") Long id);
    
    /**
     * 查询所有专辑
     */
    List<Album> selectAll();
    
    /**
     * 插入专辑
     */
    int insertAlbum(Album album);
    
    /**
     * 批量插入专辑
     */
    int insertBatch(@Param("list") List<Album> albums);
    
    /**
     * 根据歌手ID查询专辑列表
     */
    List<Album> selectByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 分页查询专辑
     */
    List<Album> selectPage(@Param("name") String name, 
                          @Param("artistId") Long artistId,
                          @Param("releaseYear") Integer releaseYear,
                          @Param("offset") Long offset, 
                          @Param("size") Long size);
    
    /**
     * 统计专辑数量
     */
    Long countAlbums(@Param("name") String name, 
                     @Param("artistId") Long artistId,
                     @Param("releaseYear") Integer releaseYear);
    
    /**
     * 更新专辑信息
     */
    int updateAlbum(Album album);
    
    /**
     * 逻辑删除专辑
     */
    int deleteAlbum(@Param("id") Long id);
    
    /**
     * 批量逻辑删除专辑
     */
    int deleteBatch(@Param("list") List<Long> ids);
    
    /**
     * 搜索专辑
     */
    List<Album> searchAlbums(@Param("keyword") String keyword, 
                            @Param("offset") Long offset, 
                            @Param("size") Long size);
    
    /**
     * 获取最新专辑
     */
    List<Album> selectLatestAlbums(@Param("limit") Integer limit);
}