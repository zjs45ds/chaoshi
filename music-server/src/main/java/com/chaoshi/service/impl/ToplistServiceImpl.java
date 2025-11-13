package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Song;
import com.chaoshi.entity.Toplist;
import com.chaoshi.entity.ToplistSong;
import com.chaoshi.mapper.SongMapper;
import com.chaoshi.mapper.ToplistMapper;
import com.chaoshi.mapper.ToplistSongMapper;
import com.chaoshi.service.ToplistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ToplistServiceImpl implements ToplistService {
    
    private final ToplistMapper toplistMapper;
    private final ToplistSongMapper toplistSongMapper;
    private final SongMapper songMapper;
    
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
    
    // ==================== 排行榜歌曲相关方法实现 ====================
    
    @Override
    public PageResult<Song> getToplistSongs(Long toplistId, Integer page, Integer size) {
        try {
            // 验证排行榜是否存在
            getToplistById(toplistId);
            
            long offset = (long) (page - 1) * size;
            
            // 获取排行榜歌曲关联信息（按排名排序）
            List<ToplistSong> toplistSongs = toplistSongMapper.selectByToplistIdPage(toplistId, (int) offset, size);
            
            // 获取歌曲详细信息
            List<Song> songs = new ArrayList<>();
            for (ToplistSong toplistSong : toplistSongs) {
                Song song = songMapper.selectById(toplistSong.getSongId());
                if (song != null) {
                    songs.add(song);
                }
            }
            
            // 获取总数
            Long total = toplistSongMapper.countByToplistId(toplistId);
            
            log.info("获取排行榜歌曲成功: 排行榜ID={}, 歌曲数量={}", toplistId, songs.size());
            return new PageResult<>(total, songs, page, size);
        } catch (Exception e) {
            log.error("获取排行榜歌曲失败: 排行榜ID={}", toplistId, e);
            throw new RuntimeException("获取排行榜歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean addSongToToplist(Long toplistId, Long songId, Integer rank) {
        try {
            // 验证排行榜和歌曲是否存在
            getToplistById(toplistId);
            Song song = songMapper.selectById(songId);
            if (song == null) {
                throw new RuntimeException("歌曲不存在: " + songId);
            }
            
            // 创建排行榜歌曲关联
            ToplistSong toplistSong = new ToplistSong();
            toplistSong.setToplistId(toplistId);
            toplistSong.setSongId(songId);
            toplistSong.setRank(rank);
            toplistSong.setCreatedAt(LocalDateTime.now());
            toplistSong.setUpdatedAt(LocalDateTime.now());
            toplistSong.setDeleted(0);
            
            int result = toplistSongMapper.insertToplistSong(toplistSong);
            
            log.info("添加歌曲到排行榜成功: 排行榜ID={}, 歌曲ID={}, 排名={}", toplistId, songId, rank);
            return result > 0;
        } catch (Exception e) {
            log.error("添加歌曲到排行榜失败: 排行榜ID={}, 歌曲ID={}", toplistId, songId, e);
            throw new RuntimeException("添加歌曲到排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean addSongsToToplist(Long toplistId, List<ToplistSong> toplistSongs) {
        try {
            // 验证排行榜是否存在
            getToplistById(toplistId);
            
            // 设置排行榜ID和时间戳
            for (ToplistSong toplistSong : toplistSongs) {
                toplistSong.setToplistId(toplistId);
                toplistSong.setCreatedAt(LocalDateTime.now());
                toplistSong.setUpdatedAt(LocalDateTime.now());
                toplistSong.setDeleted(0);
                
                // 验证歌曲是否存在
                Song song = songMapper.selectById(toplistSong.getSongId());
                if (song == null) {
                    throw new RuntimeException("歌曲不存在: " + toplistSong.getSongId());
                }
            }
            
            int result = toplistSongMapper.insertBatch(toplistSongs);
            
            log.info("批量添加歌曲到排行榜成功: 排行榜ID={}, 歌曲数量={}", toplistId, toplistSongs.size());
            return result > 0;
        } catch (Exception e) {
            log.error("批量添加歌曲到排行榜失败: 排行榜ID={}", toplistId, e);
            throw new RuntimeException("批量添加歌曲到排行榜失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean removeSongFromToplist(Long toplistId, Long songId) {
        try {
            // 验证排行榜是否存在
            getToplistById(toplistId);
            
            // 查找排行榜歌曲关联
            List<ToplistSong> toplistSongs = toplistSongMapper.selectByToplistId(toplistId);
            ToplistSong targetSong = null;
            for (ToplistSong ts : toplistSongs) {
                if (ts.getSongId().equals(songId)) {
                    targetSong = ts;
                    break;
                }
            }
            
            if (targetSong == null) {
                throw new RuntimeException("歌曲不在排行榜中: " + songId);
            }
            
            int result = toplistSongMapper.deleteToplistSong(targetSong.getId());
            
            log.info("从排行榜移除歌曲成功: 排行榜ID={}, 歌曲ID={}", toplistId, songId);
            return result > 0;
        } catch (Exception e) {
            log.error("从排行榜移除歌曲失败: 排行榜ID={}, 歌曲ID={}", toplistId, songId, e);
            throw new RuntimeException("从排行榜移除歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateSongRank(Long toplistId, Long songId, Integer newRank) {
        try {
            // 验证排行榜是否存在
            getToplistById(toplistId);
            
            // 查找排行榜歌曲关联
            List<ToplistSong> toplistSongs = toplistSongMapper.selectByToplistId(toplistId);
            ToplistSong targetSong = null;
            for (ToplistSong ts : toplistSongs) {
                if (ts.getSongId().equals(songId)) {
                    targetSong = ts;
                    break;
                }
            }
            
            if (targetSong == null) {
                throw new RuntimeException("歌曲不在排行榜中: " + songId);
            }
            
            // 更新排名
            targetSong.setRank(newRank);
            targetSong.setUpdatedAt(LocalDateTime.now());
            int result = toplistSongMapper.updateToplistSong(targetSong);
            
            log.info("更新排行榜歌曲排名成功: 排行榜ID={}, 歌曲ID={}, 新排名={}", toplistId, songId, newRank);
            return result > 0;
        } catch (Exception e) {
            log.error("更新排行榜歌曲排名失败: 排行榜ID={}, 歌曲ID={}", toplistId, songId, e);
            throw new RuntimeException("更新排行榜歌曲排名失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean clearToplistSongs(Long toplistId) {
        try {
            // 验证排行榜是否存在
            getToplistById(toplistId);
            
            int result = toplistSongMapper.deleteByToplistId(toplistId);
            
            log.info("清空排行榜歌曲成功: 排行榜ID={}", toplistId);
            return result >= 0; // 即使没有歌曲也返回true
        } catch (Exception e) {
            log.error("清空排行榜歌曲失败: 排行榜ID={}", toplistId, e);
            throw new RuntimeException("清空排行榜歌曲失败: " + e.getMessage());
        }
    }
}