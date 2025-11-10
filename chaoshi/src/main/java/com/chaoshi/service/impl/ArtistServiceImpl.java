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
            List<Artist> artists = artistMapper.selectAll();
            if (name != null && !name.trim().isEmpty()) {
                artists = artists.stream()
                    .filter(artist -> artist.getName().contains(name))
                    .collect(java.util.stream.Collectors.toList());
            }
            return new PageResult<>((long)artists.size(), artists, page, size);
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