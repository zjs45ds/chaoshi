package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Song;
import org.example.chaoshi.entity.Toplist;
import org.example.chaoshi.entity.ToplistSong;

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
    
    // ==================== 排行榜歌曲相关方法 ====================
    
    /**
     * 获取排行榜歌曲列表
     */
    PageResult<Song> getToplistSongs(Long toplistId, Integer page, Integer size);
    
    /**
     * 添加歌曲到排行榜
     */
    boolean addSongToToplist(Long toplistId, Long songId, Integer rank);
    
    /**
     * 批量添加歌曲到排行榜
     */
    boolean addSongsToToplist(Long toplistId, List<ToplistSong> toplistSongs);
    
    /**
     * 从排行榜移除歌曲
     */
    boolean removeSongFromToplist(Long toplistId, Long songId);
    
    /**
     * 更新排行榜歌曲排名
     */
    boolean updateSongRank(Long toplistId, Long songId, Integer newRank);
    
    /**
     * 清空排行榜所有歌曲
     */
    boolean clearToplistSongs(Long toplistId);
}