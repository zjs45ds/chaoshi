package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.UserFavoriteMv;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收藏MV Mapper接口
 */
@Mapper
public interface UserFavoriteMvMapper {
    
    /**
     * 根据ID查询用户收藏MV
     */
    UserFavoriteMv selectById(@Param("id") Long id);
    
    /**
     * 查询所有用户收藏MV
     */
    List<UserFavoriteMv> selectAll();
    
    /**
     * 根据用户ID查询收藏MV
     */
    List<UserFavoriteMv> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否收藏了指定MV
     */
    Boolean existsByUserIdAndMvId(@Param("userId") Long userId, @Param("mvId") Long mvId);
    
    /**
     * 插入收藏记录
     */
    int insert(UserFavoriteMv userFavoriteMv);
    
    /**
     * 逻辑删除收藏记录（取消收藏）
     */
    int deleteByUserIdAndMvId(@Param("userId") Long userId, @Param("mvId") Long mvId);
    
    /**
     * 恢复已删除的收藏记录
     */
    int restoreByUserIdAndMvId(@Param("userId") Long userId, @Param("mvId") Long mvId, @Param("createdAt") LocalDateTime createdAt);
    
    /**
     * 物理删除收藏记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 分页查询用户收藏MV
     */
    List<UserFavoriteMv> selectPageByUserId(@Param("userId") Long userId, 
                                          @Param("offset") Integer offset, 
                                          @Param("size") Integer size);
    
    /**
     * 统计用户收藏MV数量
     */
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 批量插入收藏记录
     */
    int batchInsert(@Param("list") List<UserFavoriteMv> list);
    
    /**
     * 批量删除收藏记录
     */
    int batchDeleteByUserIdAndMvIds(@Param("userId") Long userId, @Param("mvIds") List<Long> mvIds);
}