package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.UserFavoriteAlbum;

import java.util.List;

/**
 * 用户收藏专辑Mapper接口
 */
@Mapper
public interface UserFavoriteAlbumMapper {
    
    /**
     * 根据ID查询用户收藏专辑
     */
    UserFavoriteAlbum selectById(@Param("id") Long id);
    
    /**
     * 查询所有用户收藏专辑
     */
    List<UserFavoriteAlbum> selectAll();
    
    /**
     * 根据用户ID查询收藏专辑
     */
    List<UserFavoriteAlbum> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否收藏了指定专辑
     */
    Boolean existsByUserIdAndAlbumId(@Param("userId") Long userId, @Param("albumId") Long albumId);
    
    /**
     * 插入收藏记录
     */
    int insert(UserFavoriteAlbum userFavoriteAlbum);
    
    /**
     * 逻辑删除收藏记录（取消收藏）
     */
    int deleteByUserIdAndAlbumId(@Param("userId") Long userId, @Param("albumId") Long albumId);
    
    /**
     * 物理删除收藏记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 分页查询用户收藏专辑
     */
    List<UserFavoriteAlbum> selectPageByUserId(@Param("userId") Long userId, 
                                             @Param("offset") Integer offset, 
                                             @Param("size") Integer size);
    
    /**
     * 统计用户收藏专辑数量
     */
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 批量插入收藏记录
     */
    int batchInsert(@Param("list") List<UserFavoriteAlbum> list);
    
    /**
     * 批量删除收藏记录
     */
    int batchDeleteByUserIdAndAlbumIds(@Param("userId") Long userId, @Param("albumIds") List<Long> albumIds);
}
