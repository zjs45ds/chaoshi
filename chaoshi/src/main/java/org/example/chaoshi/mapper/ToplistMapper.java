package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.Toplist;

import java.util.List;

/**
 * 排行榜Mapper接口
 */
@Mapper
public interface ToplistMapper {
    
    /**
     * 根据ID查询排行榜
     */
    Toplist selectById(@Param("id") Long id);
    
    /**
     * 查询所有排行榜
     */
    List<Toplist> selectAll();
    
    /**
     * 插入排行榜
     */
    int insertToplist(Toplist toplist);
    
    /**
     * 批量插入排行榜
     */
    int insertBatch(@Param("list") List<Toplist> toplists);
    
    /**
     * 分页查询排行榜
     */
    List<Toplist> selectPage(@Param("name") String name, 
                           @Param("offset") Long offset, 
                           @Param("size") Long size);
    
    /**
     * 统计排行榜数量
     */
    Long countToplists(@Param("name") String name);
    
    /**
     * 更新排行榜信息
     */
    int updateToplist(Toplist toplist);
    
    /**
     * 逻辑删除排行榜
     */
    int deleteToplist(@Param("id") Long id);
    
    /**
     * 批量逻辑删除排行榜
     */
    int deleteBatch(@Param("list") List<Long> ids);
    
    /**
     * 搜索排行榜
     */
    List<Toplist> searchToplists(@Param("keyword") String keyword, 
                               @Param("offset") Long offset, 
                               @Param("size") Long size);
    
    /**
     * 获取热门排行榜
     */
    List<Toplist> selectHotToplists(@Param("limit") Integer limit);
    
    /**
     * 获取最新排行榜
     */
    List<Toplist> selectLatestToplists(@Param("limit") Integer limit);
}