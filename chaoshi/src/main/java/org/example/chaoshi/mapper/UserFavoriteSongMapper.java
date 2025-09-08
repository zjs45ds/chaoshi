package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.UserFavoriteSong;

import java.util.List;

/**
 * 用户收藏歌曲Mapper接口
 */
@Mapper
public interface UserFavoriteSongMapper {
    
    /**
     * 根据ID查询用户收藏歌曲
     */
    UserFavoriteSong selectById(@Param("id") Long id);
    
    /**
     * 查询所有用户收藏歌曲
     */
    List<UserFavoriteSong> selectAll();
    
    /**
     * 根据用户ID查询收藏歌曲
     */
    List<UserFavoriteSong> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否收藏了指定歌曲
     */
    Boolean existsByUserIdAndSongId(@Param("userId") Long userId, @Param("songId") Long songId);
    
    /**
     * 插入收藏记录
     */
    int insert(UserFavoriteSong userFavoriteSong);
    
    /**
     * 逻辑删除收藏记录（取消收藏）
     */
    int deleteByUserIdAndSongId(@Param("userId") Long userId, @Param("songId") Long songId);
    
    /**
     * 物理删除收藏记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 分页查询用户收藏歌曲
     */
    List<UserFavoriteSong> selectPageByUserId(@Param("userId") Long userId, 
                                             @Param("offset") Integer offset, 
                                             @Param("size") Integer size);
    
    /**
     * 统计用户收藏歌曲数量
     */
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 批量插入收藏记录
     */
    int batchInsert(@Param("list") List<UserFavoriteSong> list);
    
    /**
     * 批量删除收藏记录
     */
    int batchDeleteByUserIdAndSongIds(@Param("userId") Long userId, @Param("songIds") List<Long> songIds);
    
}