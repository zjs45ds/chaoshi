package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.ToplistSong;

import java.util.List;

/**
 * 排行榜歌曲关联Mapper
 */
@Mapper
public interface ToplistSongMapper {
    
    /**
     * 插入排行榜歌曲关联
     */
    int insertToplistSong(ToplistSong toplistSong);
    
    /**
     * 批量插入排行榜歌曲关联
     */
    int insertBatch(@Param("list") List<ToplistSong> toplistSongs);
    
    /**
     * 根据ID查询排行榜歌曲关联
     */
    ToplistSong selectById(Long id);
    
    /**
     * 根据排行榜ID查询歌曲关联
     */
    List<ToplistSong> selectByToplistId(@Param("toplistId") Long toplistId);
    
    /**
     * 根据排行榜ID分页查询歌曲关联
     */
    List<ToplistSong> selectByToplistIdPage(@Param("toplistId") Long toplistId, 
                                           @Param("offset") Integer offset, 
                                           @Param("size") Integer size);
    
    /**
     * 统计排行榜歌曲数量
     */
    Long countByToplistId(@Param("toplistId") Long toplistId);
    
    /**
     * 更新排行榜歌曲关联
     */
    int updateToplistSong(ToplistSong toplistSong);
    
    /**
     * 删除排行榜歌曲关联
     */
    int deleteToplistSong(Long id);
    
    /**
     * 根据排行榜ID删除所有歌曲关联
     */
    int deleteByToplistId(@Param("toplistId") Long toplistId);
    
    /**
     * 批量删除排行榜歌曲关联
     */
    int deleteBatch(@Param("list") List<Long> ids);
}
