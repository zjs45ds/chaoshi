package org.example.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Album;
import org.example.chaoshi.mapper.AlbumMapper;
import org.example.chaoshi.service.AlbumService;
import org.example.chaoshi.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专辑服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumServiceImpl implements AlbumService {
    
    private final AlbumMapper albumMapper;
    private final FileUploadService fileUploadService;
    
    @Override
    @Transactional
    public Album createAlbum(Album album, MultipartFile coverFile) {
        try {
            // 上传封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                String coverUrl = fileUploadService.uploadFile(coverFile, "image");
                album.setCover(coverUrl);
            }
            
            album.setCreatedAt(LocalDateTime.now());
            album.setUpdatedAt(LocalDateTime.now());
            albumMapper.insertAlbum(album);
            
            log.info("专辑创建成功: {}", album.getId());
            return album;
        } catch (Exception e) {
            log.error("创建专辑失败", e);
            throw new RuntimeException("创建专辑失败: " + e.getMessage());
        }
    }
    
    @Override
    public Album getAlbumById(Long id) {
        Album album = albumMapper.selectById(id);
        if (album == null) {
            throw new RuntimeException("专辑不存在: " + id);
        }
        return album;
    }
    
    @Override
    @Transactional
    public Album updateAlbum(Long id, Album album, MultipartFile coverFile) {
        try {
            Album existingAlbum = getAlbumById(id);
            
            // 上传新封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                // 删除旧封面
                if (existingAlbum.getCover() != null) {
                    fileUploadService.deleteFile(existingAlbum.getCover());
                }
                String coverUrl = fileUploadService.uploadFile(coverFile, "image");
                album.setCover(coverUrl);
            }
            
            album.setId(id);
            album.setUpdatedAt(LocalDateTime.now());
            albumMapper.updateAlbum(album);
            
            log.info("专辑更新成功: {}", id);
            return getAlbumById(id);
        } catch (Exception e) {
            log.error("更新专辑失败: {}", id, e);
            throw new RuntimeException("更新专辑失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteAlbum(Long id) {
        try {
            Album album = getAlbumById(id);
            
            // 删除封面文件
            if (album.getCover() != null) {
                fileUploadService.deleteFile(album.getCover());
            }
            
            int result = albumMapper.deleteAlbum(id);
            
            log.info("专辑删除成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除专辑失败: {}", id, e);
            throw new RuntimeException("删除专辑失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteAlbums(List<Long> ids) {
        try {
            // 删除相关文件
            for (Long id : ids) {
                Album album = albumMapper.selectById(id);
                if (album != null && album.getCover() != null) {
                    fileUploadService.deleteFile(album.getCover());
                }
            }
            
            int result = albumMapper.deleteBatch(ids);
            
            log.info("批量删除专辑成功，数量: {}", ids.size());
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除专辑失败", e);
            throw new RuntimeException("批量删除专辑失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Album> getAlbumPage(String name, Long artistId, Integer releaseYear, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Album> albums = albumMapper.selectPage(name, artistId, releaseYear, offset, (long) size);
            Long total = albumMapper.countAlbums(name, artistId, releaseYear);
            
            return new PageResult<>(total, albums, page, size);
        } catch (Exception e) {
            log.error("分页查询专辑失败", e);
            throw new RuntimeException("分页查询专辑失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Album> getAlbumsByArtistId(Long artistId) {
        try {
            return albumMapper.selectByArtistId(artistId);
        } catch (Exception e) {
            log.error("根据歌手ID查询专辑失败: {}", artistId, e);
            throw new RuntimeException("根据歌手ID查询专辑失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Album> searchAlbums(String keyword, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Album> albums = albumMapper.searchAlbums(keyword, offset, (long) size);
            // 注意：搜索的总数需要单独查询
            Long total = albumMapper.countAlbums(keyword, null, null);
            
            return new PageResult<>(total, albums, page, size);
        } catch (Exception e) {
            log.error("搜索专辑失败", e);
            throw new RuntimeException("搜索专辑失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Album> getLatestAlbums(Integer limit) {
        try {
            return albumMapper.selectLatestAlbums(limit);
        } catch (Exception e) {
            log.error("获取最新专辑失败", e);
            throw new RuntimeException("获取最新专辑失败: " + e.getMessage());
        }
    }
}