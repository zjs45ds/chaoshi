package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.SearchHistory;

import java.util.List;

/**
 * 搜索历史Mapper接口
 */
@Mapper
public interface SearchHistoryMapper {
    
    /**
     * 根据用户ID查询搜索历史
     */
    List<SearchHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 根据用户ID和关键词查询搜索历史
     */
    SearchHistory selectByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    /**
     * 插入搜索历史
     */
    int insertSearchHistory(SearchHistory searchHistory);
    
    /**
     * 更新搜索历史
     */
    int updateSearchHistory(SearchHistory searchHistory);
    
    /**
     * 删除用户的单个搜索历史
     */
    int deleteByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    /**
     * 清空用户的所有搜索历史
     */
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 获取热门搜索关键词
     */
    List<SearchHistory> selectHotKeywords(@Param("limit") Integer limit);
}
