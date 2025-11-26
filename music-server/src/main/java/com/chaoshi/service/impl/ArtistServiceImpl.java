package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.PageResult;
import com.chaoshi.dto.response.AlbumResponse;
import com.chaoshi.dto.response.SongResponse;
import com.chaoshi.entity.Artist;
import com.chaoshi.mapper.ArtistMapper;
import com.chaoshi.service.ArtistService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    
    private final ArtistMapper artistMapper;

    @Override
    public Artist getArtistById(Long id) {
        try {
            Artist artist = artistMapper.selectById(id);
            if (artist == null) {
                throw new RuntimeException("歌手不存在");
            }
            return artist;
        } catch (Exception e) {
            log.error("获取歌手失败: {}", id, e);
            throw new RuntimeException("获取歌手失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Artist> getArtistPage(String name, Integer page, Integer size) {
        try {
            // 计算偏移量，转换为Long类型以匹配Mapper接口
            long offset = (long)(page - 1) * size;
            long pageSize = size;
            
            // 使用数据库分页查询，保持数据库排序顺序
            List<Artist> artists = artistMapper.selectPage(name, offset, pageSize);
            
            // 获取总记录数
            long total = artistMapper.countArtists(name);
            
            return new PageResult<>(total, artists, page, size);
        } catch (Exception e) {
            log.error("分页获取歌手失败", e);
            throw new RuntimeException("分页获取歌手失败: " + e.getMessage());
        }
    }

    @Override
    public List<Artist> getPopularArtists(Integer limit) {
        try {
            List<Artist> artists = artistMapper.selectAll();
            return artists.stream()
                .limit(limit != null ? limit : 10)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("获取热门歌手失败", e);
            throw new RuntimeException("获取热门歌手失败: " + e.getMessage());
        }
    }

    @Override
    public List<AlbumResponse> getXueZhiQianAlbums() {
        return new ArrayList<>();
    }

    @Override
    public List<SongResponse> getXueZhiQianHotSongs() {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getXueZhiQianStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("albumCount", 0);
        stats.put("songCount", 0);
        stats.put("mvCount", 0);
        stats.put("fanCount", 0);
        return stats;
    }
}