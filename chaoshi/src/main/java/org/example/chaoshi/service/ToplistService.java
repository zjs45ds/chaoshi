package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Toplist;

import java.util.List;

/**
 * 排行榜Service接口
 */
public interface ToplistService {
    
    /**
     * 创建排行榜
     */
    Toplist createToplist(Toplist toplist);
    
    /**
     * 根据ID获取排行榜
     */
    Toplist getToplistById(Long id);
    
    /**
     * 更新排行榜信息
     */
    Toplist updateToplist(Long id, Toplist toplist);
    
    /**
     * 删除排行榜
     */
    boolean deleteToplist(Long id);
    
    /**
     * 批量删除排行榜
     */
    boolean deleteToplists(List<Long> ids);
    
    /**
     * 分页查询排行榜
     */
    PageResult<Toplist> getToplistPage(String name, Integer page, Integer size);
    
    /**
     * 获取所有排行榜
     */
    List<Toplist> getAllToplists();
    
    /**
     * 搜索排行榜
     */
    PageResult<Toplist> searchToplists(String keyword, Integer page, Integer size);
    
    /**
     * 获取热门排行榜
     */
    List<Toplist> getHotToplists(Integer limit);
    
    /**
     * 获取最新排行榜
     */
    List<Toplist> getLatestToplists(Integer limit);
}