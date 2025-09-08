package org.example.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Toplist;
import org.example.chaoshi.mapper.ToplistMapper;
import org.example.chaoshi.service.ToplistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 排行榜服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ToplistServiceImpl implements ToplistService {
    
    private final ToplistMapper toplistMapper;
    
    @Override
    @Transactional
    public Toplist createToplist(Toplist toplist) {
        try {
            toplist.setCreatedAt(LocalDateTime.now());
            toplist.setUpdatedAt(LocalDateTime.now());
            toplist.setDeleted(0);
            toplistMapper.insertToplist(toplist);
            
            log.info("排行榜创建成功: {}", toplist.getId());
            return toplist;
        } catch (Exception e) {
            log.error("创建排行榜失败", e);
            throw new RuntimeException("创建排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    public Toplist getToplistById(Long id) {
        Toplist toplist = toplistMapper.selectById(id);
        if (toplist == null) {
            throw new RuntimeException("排行榜不存在: " + id);
        }
        return toplist;
    }
    
    @Override
    @Transactional
    public Toplist updateToplist(Long id, Toplist toplist) {
        try {
            // 检查排行榜是否存在
            getToplistById(id);
            
            toplist.setId(id);
            toplist.setUpdatedAt(LocalDateTime.now());
            toplistMapper.updateToplist(toplist);
            
            log.info("排行榜更新成功: {}", id);
            return getToplistById(id);
        } catch (Exception e) {
            log.error("更新排行榜失败: {}", id, e);
            throw new RuntimeException("更新排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteToplist(Long id) {
        try {
            // 检查排行榜是否存在
            getToplistById(id);
            
            int result = toplistMapper.deleteToplist(id);
            
            log.info("排行榜删除成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除排行榜失败: {}", id, e);
            throw new RuntimeException("删除排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteToplists(List<Long> ids) {
        try {
            int result = toplistMapper.deleteBatch(ids);
            
            log.info("批量删除排行榜成功，数量: {}", ids.size());
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除排行榜失败", e);
            throw new RuntimeException("批量删除排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Toplist> getToplistPage(String name, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Toplist> toplists = toplistMapper.selectPage(name, offset, (long) size);
            Long total = toplistMapper.countToplists(name);
            
            return new PageResult<>(total, toplists, page, size);
        } catch (Exception e) {
            log.error("分页查询排行榜失败", e);
            throw new RuntimeException("分页查询排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Toplist> getAllToplists() {
        try {
            return toplistMapper.selectAll();
        } catch (Exception e) {
            log.error("查询所有排行榜失败", e);
            throw new RuntimeException("查询所有排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Toplist> searchToplists(String keyword, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Toplist> toplists = toplistMapper.searchToplists(keyword, offset, (long) size);
            // 注意：搜索的总数需要单独查询
            Long total = toplistMapper.countToplists(keyword);
            
            return new PageResult<>(total, toplists, page, size);
        } catch (Exception e) {
            log.error("搜索排行榜失败", e);
            throw new RuntimeException("搜索排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Toplist> getHotToplists(Integer limit) {
        try {
            return toplistMapper.selectHotToplists(limit);
        } catch (Exception e) {
            log.error("获取热门排行榜失败", e);
            throw new RuntimeException("获取热门排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Toplist> getLatestToplists(Integer limit) {
        try {
            return toplistMapper.selectLatestToplists(limit);
        } catch (Exception e) {
            log.error("获取最新排行榜失败", e);
            throw new RuntimeException("获取最新排行榜失败: " + e.getMessage());
        }
    }
}