package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.UserFavoriteMv;

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
    
}