package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import com.chaoshi.entity.SearchHistory;
import com.chaoshi.mapper.SearchHistoryMapper;
import com.chaoshi.service.SearchHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 搜索历史Service实现类
 */
@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImpl implements SearchHistoryService {
    
    private final SearchHistoryMapper searchHistoryMapper;
    
    @Override
    @Transactional
    public void saveSearchHistory(Long userId, String keyword) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        
        keyword = keyword.trim();
        
        // 查询是否已存在该关键词的搜索记录
        SearchHistory existingHistory = searchHistoryMapper.selectByUserIdAndKeyword(userId, keyword);
        
        if (existingHistory != null) {
            // 如果存在，增加搜索次数
            existingHistory.setSearchCount(existingHistory.getSearchCount() + 1);
            searchHistoryMapper.updateSearchHistory(existingHistory);
        } else {
            // 如果不存在，创建新记录
            SearchHistory newHistory = new SearchHistory();
            newHistory.setUserId(userId);
            newHistory.setKeyword(keyword);
            newHistory.setSearchCount(1);
            newHistory.setDeleted(0);
            searchHistoryMapper.insertSearchHistory(newHistory);
        }
    }
    
    @Override
    public List<SearchHistory> getUserSearchHistory(Long userId, Integer limit) {
        if (userId == null) {
            return List.of();
        }
        
        if (limit == null || limit <= 0) {
            limit = 20; // 默认返回20条
        }
        
        return searchHistoryMapper.selectByUserId(userId, limit);
    }
    
    @Override
    @Transactional
    public void deleteSearchHistoryItem(Long userId, String keyword) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            System.out.println("删除搜索历史失败: 用户ID或关键词为空");
            return;
        }
        
        System.out.println("正在删除搜索历史: userId=" + userId + ", keyword=" + keyword.trim());
        int result = searchHistoryMapper.deleteByUserIdAndKeyword(userId, keyword.trim());
        System.out.println("删除搜索历史结果: 影响行数=" + result);
    }
    
    @Override
    @Transactional
    public void clearUserSearchHistory(Long userId) {
        if (userId == null) {
            System.out.println("清空搜索历史失败: 用户ID为空");
            return;
        }
        
        System.out.println("正在清空用户搜索历史: userId=" + userId);
        int result = searchHistoryMapper.deleteByUserId(userId);
        System.out.println("清空搜索历史结果: 影响行数=" + result);
    }
    
    @Override
    public List<SearchHistory> getHotKeywords(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10条
        }
        
        return searchHistoryMapper.selectHotKeywords(limit);
    }
}
