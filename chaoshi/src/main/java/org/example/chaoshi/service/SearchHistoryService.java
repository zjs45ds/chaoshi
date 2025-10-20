package org.example.chaoshi.service;

import org.example.chaoshi.entity.SearchHistory;

import java.util.List;

/**
 * 搜索历史Service接口
 */
public interface SearchHistoryService {
    
    /**
     * 保存搜索历史
     * @param userId 用户ID
     * @param keyword 搜索关键词
     */
    void saveSearchHistory(Long userId, String keyword);
    
    /**
     * 获取用户搜索历史
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 搜索历史列表
     */
    List<SearchHistory> getUserSearchHistory(Long userId, Integer limit);
    
    /**
     * 删除用户的单个搜索历史
     * @param userId 用户ID
     * @param keyword 搜索关键词
     */
    void deleteSearchHistoryItem(Long userId, String keyword);
    
    /**
     * 清空用户的所有搜索历史
     * @param userId 用户ID
     */
    void clearUserSearchHistory(Long userId);
    
    /**
     * 获取热门搜索关键词
     * @param limit 限制数量
     * @return 热门搜索列表
     */
    List<SearchHistory> getHotKeywords(Integer limit);
}
