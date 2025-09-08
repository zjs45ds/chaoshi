package org.example.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.dto.response.AlbumResponse;
import org.example.chaoshi.dto.response.SongResponse;
import org.example.chaoshi.entity.Artist;
import org.example.chaoshi.entity.ArtistTimeline;
import org.example.chaoshi.mapper.ArtistMapper;
import org.example.chaoshi.service.ArtistService;
import org.example.chaoshi.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 歌手服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistServiceImpl implements ArtistService {
    
    private final ArtistMapper artistMapper;
    private final FileUploadService fileUploadService;
    
    @Override
    @Transactional
    public Artist createArtist(Artist artist, MultipartFile avatarFile) {
        try {
            // 上传头像图片
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = fileUploadService.uploadFile(avatarFile, "image");
                artist.setAvatar(avatarUrl);
            }
            
            artist.setCreatedAt(LocalDateTime.now());
            artist.setUpdatedAt(LocalDateTime.now());
            artistMapper.insertArtist(artist);
            
            log.info("歌手创建成功: {}", artist.getId());
            return artist;
        } catch (Exception e) {
            log.error("创建歌手失败", e);
            throw new RuntimeException("创建歌手失败: " + e.getMessage());
        }
    }
    
    @Override
    public Artist getArtistById(Long id) {
        Artist artist = artistMapper.selectById(id);
        if (artist == null) {
            throw new RuntimeException("歌手不存在: " + id);
        }
        return artist;
    }
    
    @Override
    public Artist getArtistByName(String name) {
        return artistMapper.selectByName(name);
    }
    
    @Override
    @Transactional
    public Artist updateArtist(Long id, Artist artist, MultipartFile avatarFile) {
        try {
            Artist existingArtist = getArtistById(id);
            
            // 上传新头像图片
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // 删除旧头像
                if (existingArtist.getAvatar() != null) {
                    fileUploadService.deleteFile(existingArtist.getAvatar());
                }
                String avatarUrl = fileUploadService.uploadFile(avatarFile, "image");
                artist.setAvatar(avatarUrl);
            }
            
            artist.setId(id);
            artist.setUpdatedAt(LocalDateTime.now());
            artistMapper.updateArtist(artist);
            
            log.info("歌手更新成功: {}", id);
            return getArtistById(id);
        } catch (Exception e) {
            log.error("更新歌手失败: {}", id, e);
            throw new RuntimeException("更新歌手失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteArtist(Long id) {
        try {
            Artist artist = getArtistById(id);
            
            // 删除头像文件
            if (artist.getAvatar() != null) {
                fileUploadService.deleteFile(artist.getAvatar());
            }
            
            int result = artistMapper.deleteArtist(id);
            
            log.info("歌手删除成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除歌手失败: {}", id, e);
            throw new RuntimeException("删除歌手失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteArtists(List<Long> ids) {
        try {
            // 删除相关文件
            for (Long id : ids) {
                Artist artist = artistMapper.selectById(id);
                if (artist != null && artist.getAvatar() != null) {
                    fileUploadService.deleteFile(artist.getAvatar());
                }
            }
            
            int result = artistMapper.deleteBatch(ids);
            
            log.info("批量删除歌手成功，数量: {}", ids.size());
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除歌手失败", e);
            throw new RuntimeException("批量删除歌手失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Artist> getArtistPage(String name, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Artist> artists = artistMapper.selectPage(name, offset, (long) size);
            Long total = artistMapper.countArtists(name);
            
            return new PageResult<>(total, artists, page, size);
        } catch (Exception e) {
            log.error("分页查询歌手失败", e);
            throw new RuntimeException("分页查询歌手失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Artist> searchArtists(String keyword, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Artist> artists = artistMapper.searchArtists(keyword, offset, (long) size);
            // 注意：搜索的总数需要单独查询
            Long total = artistMapper.countArtists(keyword);
            
            return new PageResult<>(total, artists, page, size);
        } catch (Exception e) {
            log.error("搜索歌手失败", e);
            throw new RuntimeException("搜索歌手失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Artist> getPopularArtists(Integer limit) {
        try {
            return artistMapper.selectPopularArtists(limit);
        } catch (Exception e) {
            log.error("获取热门歌手失败", e);
            throw new RuntimeException("获取热门歌手失败: " + e.getMessage());
        }
    }
    
    // 以下是保留的原有方法，用于特定歌手的数据展示
    @Override
    public List<AlbumResponse> getXueZhiQianAlbums() {
        
        return List.of();
    }
    
    @Override
    public List<ArtistTimeline> getXueZhiQianTimeline() {

        return List.of();
    }
    
    @Override
    public List<SongResponse> getXueZhiQianHotSongs() {

        return List.of();
    }
    
    @Override
    public Map<String, Object> getXueZhiQianStats() {
    
        return Map.of();
    }
}