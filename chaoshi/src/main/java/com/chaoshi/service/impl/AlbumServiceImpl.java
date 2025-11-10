package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Album;
import com.chaoshi.entity.UserFavoriteAlbum;
import com.chaoshi.mapper.AlbumMapper;
import com.chaoshi.mapper.UserFavoriteAlbumMapper;
import com.chaoshi.service.AlbumService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 专辑服务实现类（无文件上传功能）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    
    private final AlbumMapper albumMapper;
    private final UserFavoriteAlbumMapper userFavoriteAlbumMapper;

    @Override
    @Transactional
    public Album createAlbum(Album album) {
        try {
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
        try {
            Album album = albumMapper.selectById(id);
            if (album == null) {
                throw new RuntimeException("专辑不存在");
            }
            return album;
        } catch (Exception e) {
            log.error("获取专辑失败: {}", id, e);
            throw new RuntimeException("获取专辑失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Album updateAlbum(Long id, Album album) {
        try {
            Album existingAlbum = getAlbumById(id);
            if (existingAlbum == null) {
                throw new RuntimeException("专辑不存在");
            }
            
            album.setId(id);
            album.setUpdatedAt(LocalDateTime.now());
            
            int result = albumMapper.updateAlbum(album);
            if (result > 0) {
                log.info("专辑更新成功: {}", id);
                return getAlbumById(id);
            } else {
                throw new RuntimeException("专辑更新失败");
            }
        } catch (Exception e) {
            log.error("更新专辑失败: {}", id, e);
            throw new RuntimeException("更新专辑失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteAlbum(Long id) {
        try {
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
            int result = albumMapper.deleteBatch(ids);
            log.info("批量删除专辑成功，数量: {}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除专辑失败", e);
            throw new RuntimeException("批量删除专辑失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Album> getAlbumPage(String name, Long artistId, Integer releaseYear, Integer page, Integer size) {
        try {
            List<Album> albums = albumMapper.selectAll();
            return new PageResult<>((long)albums.size(), albums, page, size);
        } catch (Exception e) {
            log.error("分页获取专辑失败", e);
            throw new RuntimeException("分页获取专辑失败: " + e.getMessage());
        }
    }

    @Override
    public List<Album> getAlbumsByArtistId(Long artistId) {
        try {
            return albumMapper.selectByArtistId(artistId);
        } catch (Exception e) {
            log.error("根据艺术家获取专辑失败: {}", artistId, e);
            throw new RuntimeException("根据艺术家获取专辑失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Album> searchAlbums(String keyword, Integer page, Integer size) {
        // 简化实现
        return getAlbumPage(keyword, null, null, page, size);
    }

    @Override
    public List<Album> getLatestAlbums(Integer limit) {
        try {
            List<Album> albums = albumMapper.selectAll();
            return albums.stream().limit(limit).collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("获取最新专辑失败", e);
            throw new RuntimeException("获取最新专辑失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean favoriteAlbum(Long userId, Long albumId, String action) {
        try {
            if ("like".equals(action)) {
                // 检查是否已经收藏
                Boolean exists = userFavoriteAlbumMapper.existsByUserIdAndAlbumId(userId, albumId);
                if (exists != null && exists) {
                    // 如果已经收藏，直接返回true而不抛出异常
                    log.info("专辑已经在收藏夹中，无需重复收藏: userId={}, albumId={}", userId, albumId);
                    return true;
                }
                
                UserFavoriteAlbum favorite = new UserFavoriteAlbum();
                favorite.setUserId(userId);
                favorite.setAlbumId(albumId);
                favorite.setCreatedAt(LocalDateTime.now());
                
                userFavoriteAlbumMapper.insert(favorite);
                log.info("添加专辑收藏成功: userId={}, albumId={}", userId, albumId);
                return true;
            } else if ("unlike".equals(action)) {
                // 检查是否已经收藏
                Boolean exists = userFavoriteAlbumMapper.existsByUserIdAndAlbumId(userId, albumId);
                if (exists == null || !exists) {
                    // 如果没有收藏，直接返回false而不抛出异常
                    log.info("专辑未在收藏夹中，无需取消收藏: userId={}, albumId={}", userId, albumId);
                    return false;
                }
                
                userFavoriteAlbumMapper.deleteByUserIdAndAlbumId(userId, albumId);
                log.info("取消专辑收藏成功: userId={}, albumId={}", userId, albumId);
                return false;
            }
            throw new RuntimeException("无效的操作类型: " + action);
        } catch (RuntimeException e) {
            // 对于已知的业务异常，重新抛出
            if (e.getMessage().contains("无效的操作类型")) {
                throw e;
            }
            log.error("专辑收藏操作失败: userId={}, albumId={}, action={}", userId, albumId, action, e);
            throw new RuntimeException("专辑收藏操作失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("专辑收藏操作失败: userId={}, albumId={}, action={}", userId, albumId, action, e);
            throw new RuntimeException("专辑收藏操作失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isAlbumFavorited(Long userId, Long albumId) {
        try {
            Boolean exists = userFavoriteAlbumMapper.existsByUserIdAndAlbumId(userId, albumId);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("检查专辑收藏状态失败: userId={}, albumId={}", userId, albumId, e);
            return false;
        }
    }

    @Override
    public List<Album> getUserFavoriteAlbums(Long userId) {
        try {
            List<UserFavoriteAlbum> favorites = userFavoriteAlbumMapper.selectByUserId(userId);
            List<Album> albums = new ArrayList<>();
            for (UserFavoriteAlbum favorite : favorites) {
                Album album = albumMapper.selectById(favorite.getAlbumId());
                if (album != null) {
                    albums.add(album);
                }
            }
            return albums;
        } catch (Exception e) {
            log.error("获取用户收藏专辑失败: {}", userId, e);
            throw new RuntimeException("获取用户收藏专辑失败: " + e.getMessage());
        }
    }
}
