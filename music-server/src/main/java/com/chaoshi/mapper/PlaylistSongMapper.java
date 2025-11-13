package com.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.chaoshi.entity.PlaylistSong;

import java.util.List;

/**
 * 播放列表歌曲关联Mapper接口
 */
@Mapper
public interface PlaylistSongMapper {
    
    /**
     * 根据ID查询播放列表歌曲
     */
    PlaylistSong selectById(@Param("id") Long id);
    
    /**
     * 查询所有播放列表歌曲
     */
    List<PlaylistSong> selectAll();
    
    /**
     * 添加歌曲到播放列表
     */
    int insertPlaylistSong(PlaylistSong playlistSong);
    
    /**
     * 批量添加歌曲到播放列表
     */
    int insertBatch(@Param("list") List<PlaylistSong> playlistSongs);
    
    /**
     * 根据播放列表ID查询歌曲
     */
    List<PlaylistSong> selectByPlaylistId(@Param("playlistId") Long playlistId);
    
    /**
     * 根据歌曲ID查询所在的播放列表
     */
    List<PlaylistSong> selectBySongId(@Param("songId") Long songId);
    
    /**
     * 检查歌曲是否在播放列表中
     */
    Boolean existsInPlaylist(@Param("playlistId") Long playlistId, 
                            @Param("songId") Long songId);
    
    /**
     * 获取播放列表中歌曲的最大排序号
     */
    Integer getMaxSortOrder(@Param("playlistId") Long playlistId);
    
    /**
     * 更新歌曲排序
     */
    int updateSortOrder(@Param("playlistId") Long playlistId, 
                       @Param("songId") Long songId,
                       @Param("sortOrder") Integer sortOrder);
    
    /**
     * 从播放列表中移除歌曲
     */
    int removeFromPlaylist(@Param("playlistId") Long playlistId, 
                          @Param("songId") Long songId);
    
    /**
     * 清空播放列表
     */
    int clearPlaylist(@Param("playlistId") Long playlistId);
    
    /**
     * 统计播放列表中的歌曲数量
     */
    Long countSongsInPlaylist(@Param("playlistId") Long playlistId);
    
    /**
     * 批量更新排序
     */
    int batchUpdateSortOrder(@Param("list") List<PlaylistSong> playlistSongs);
}