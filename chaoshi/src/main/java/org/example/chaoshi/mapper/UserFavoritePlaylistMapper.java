package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.UserFavoritePlaylist;

import java.util.List;

/**
 * 用户收藏歌单Mapper接口
 */
@Mapper
public interface UserFavoritePlaylistMapper {
    
    /**
     * 根据ID查询用户收藏歌单
     */
    UserFavoritePlaylist selectById(@Param("id") Long id);
    
    /**
     * 查询所有用户收藏歌单
     */
    List<UserFavoritePlaylist> selectAll();
    
}