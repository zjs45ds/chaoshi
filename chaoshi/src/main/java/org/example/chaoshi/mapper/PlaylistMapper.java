package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.Playlist;

import java.util.List;

/**
 * 播放列表Mapper接口
 */
@Mapper
public interface PlaylistMapper {
    
    /**
     * 根据ID查询播放列表
     */
    Playlist selectById(@Param("id") Long id);
    
    /**
     * 查询所有播放列表
     */
    List<Playlist> selectAll();
    
    /**
     * 插入播放列表
     */
    int insertPlaylist(Playlist playlist);
    
    /**
     * 根据用户ID查询播放列表
     */
    List<Playlist> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询公开播放列表
     */
    List<Playlist> selectPublicPlaylists(@Param("offset") Long offset, 
                                         @Param("size") Long size);
    
    /**
     * 分页查询播放列表
     */
    List<Playlist> selectPage(@Param("name") String name,
                             @Param("userId") Long userId,
                             @Param("isPublic") Boolean isPublic,
                             @Param("offset") Long offset, 
                             @Param("size") Long size);
    
    /**
     * 统计播放列表数量
     */
    Long countPlaylists(@Param("name") String name,
                        @Param("userId") Long userId,
                        @Param("isPublic") Boolean isPublic);
    
    /**
     * 更新播放列表信息
     */
    int updatePlaylist(Playlist playlist);
    
    /**
     * 逻辑删除播放列表
     */
    int deletePlaylist(@Param("id") Long id);
    
    /**
     * 搜索播放列表
     */
    List<Playlist> searchPlaylists(@Param("keyword") String keyword, 
                                  @Param("offset") Long offset, 
                                  @Param("size") Long size);
}