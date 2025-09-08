package org.example.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.dto.response.SongResponse;
import org.example.chaoshi.entity.Album;
import org.example.chaoshi.entity.Artist;
import org.example.chaoshi.entity.Song;
import org.example.chaoshi.entity.UserFavoriteSong;
import org.example.chaoshi.mapper.AlbumMapper;
import org.example.chaoshi.mapper.ArtistMapper;
import org.example.chaoshi.mapper.SongMapper;
import org.example.chaoshi.mapper.UserFavoriteSongMapper;
import org.example.chaoshi.service.FileUploadService;
import org.example.chaoshi.service.SongService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 歌曲Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
    
    private final SongMapper songMapper;
    private final AlbumMapper albumMapper;
    private final ArtistMapper artistMapper;
    private final UserFavoriteSongMapper userFavoriteSongMapper;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional
    public Song createSong(Song song, MultipartFile audioFile, MultipartFile coverFile) {
        try {
            // 上传音频文件
            if (audioFile != null && !audioFile.isEmpty()) {
                String audioUrl = fileUploadService.uploadFile(audioFile, "audio");
                song.setFilePath(audioUrl);
            }
            
            // 上传封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                String coverUrl = fileUploadService.uploadFile(coverFile, "image");
                song.setCover(coverUrl);
            }
            
            song.setCreatedAt(LocalDateTime.now());
            song.setUpdatedAt(LocalDateTime.now());
            songMapper.insertSong(song);
            
            log.info("歌曲创建成功: {}", song.getId());
            return song;
        } catch (Exception e) {
            log.error("创建歌曲失败", e);
            throw new RuntimeException("创建歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public Song getSongById(Long id) {
        Song song = songMapper.selectById(id);
        if (song == null) {
            throw new RuntimeException("歌曲不存在: " + id);
        }
        return song;
    }
    
    @Override
    @Transactional
    public Song updateSong(Long id, Song song, MultipartFile audioFile, MultipartFile coverFile) {
        try {
            Song existingSong = getSongById(id);
            
            // 上传新音频文件
            if (audioFile != null && !audioFile.isEmpty()) {
                // 删除旧音频
                if (existingSong.getFilePath() != null) {
                    fileUploadService.deleteFile(existingSong.getFilePath());
                }
                String audioUrl = fileUploadService.uploadFile(audioFile, "audio");
                song.setFilePath(audioUrl);
            }
            
            // 上传新封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                // 删除旧封面
                if (existingSong.getCover() != null) {
                    fileUploadService.deleteFile(existingSong.getCover());
                }
                String coverUrl = fileUploadService.uploadFile(coverFile, "image");
                song.setCover(coverUrl);
            }
            
            song.setId(id);
            song.setUpdatedAt(LocalDateTime.now());
            songMapper.updateSong(song);
            
            log.info("歌曲更新成功: {}", id);
            return getSongById(id);
        } catch (Exception e) {
            log.error("更新歌曲失败: {}", id, e);
            throw new RuntimeException("更新歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteSong(Long id) {
        try {
            Song song = getSongById(id);
            
            // 删除相关文件
            if (song.getFilePath() != null) {
                fileUploadService.deleteFile(song.getFilePath());
            }
            if (song.getCover() != null) {
                fileUploadService.deleteFile(song.getCover());
            }
            
            int result = songMapper.deleteSong(id);
            
            log.info("歌曲删除成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除歌曲失败: {}", id, e);
            throw new RuntimeException("删除歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteSongs(List<Long> ids) {
        try {
            // 删除相关文件
            for (Long id : ids) {
                Song song = songMapper.selectById(id);
                if (song != null) {
                    if (song.getFilePath() != null) {
                        fileUploadService.deleteFile(song.getFilePath());
                    }
                    if (song.getCover() != null) {
                        fileUploadService.deleteFile(song.getCover());
                    }
                }
            }
            
            int result = songMapper.deleteBatch(ids);
            
            log.info("批量删除歌曲成功，数量: {}", ids.size());
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除歌曲失败", e);
            throw new RuntimeException("批量删除歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Song> getSongPage(String name, Long artistId, Long albumId, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Song> songs = songMapper.selectPage(name, artistId, albumId, offset, (long) size);
            Long total = songMapper.countSongs(name, artistId, albumId);
            
            return new PageResult<>(total, songs, page, size);
        } catch (Exception e) {
            log.error("分页查询歌曲失败", e);
            throw new RuntimeException("分页查询歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Song> getSongsByArtistId(Long artistId) {
        try {
            return songMapper.selectByArtistId(artistId);
        } catch (Exception e) {
            log.error("根据歌手ID查询歌曲失败: {}", artistId, e);
            throw new RuntimeException("根据歌手ID查询歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Song> getSongsByAlbumId(Long albumId) {
        try {
            return songMapper.selectByAlbumId(albumId);
        } catch (Exception e) {
            log.error("根据专辑ID查询歌曲失败: {}", albumId, e);
            throw new RuntimeException("根据专辑ID查询歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Song> searchSongs(String keyword, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Song> songs = songMapper.searchSongs(keyword, offset, (long) size);
            // 注意：搜索的总数需要单独查询
            Long total = songMapper.countSongs(keyword, null, null);
            
            return new PageResult<>(total, songs, page, size);
        } catch (Exception e) {
            log.error("搜索歌曲失败", e);
            throw new RuntimeException("搜索歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<SongResponse> getSongList(Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Song> songs = songMapper.selectPage(null, null, null, offset, (long) size);
            Long total = songMapper.countSongs(null, null, null);
            
            List<SongResponse> songResponses = songs.stream()
                    .map(this::convertToSongResponse)
                    .collect(Collectors.toList());
            
            return new PageResult<>(total, songResponses, page, size);
        } catch (Exception e) {
            log.error("分页查询歌曲列表失败", e);
            throw new RuntimeException("分页查询歌曲列表失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteSong(Long userId, Long songId, String action) {
        try {
            if ("like".equals(action)) {
                // 检查歌曲是否存在
                Song song = songMapper.selectById(songId);
                if (song == null) {
                    throw new RuntimeException("歌曲不存在: " + songId);
                }
                
                // 创建收藏记录（使用ON DUPLICATE KEY UPDATE处理重复）
                UserFavoriteSong userFavoriteSong = new UserFavoriteSong();
                userFavoriteSong.setUserId(userId);
                userFavoriteSong.setSongId(songId);
                userFavoriteSong.setCreatedAt(LocalDateTime.now());
                userFavoriteSong.setDeleted(0);
                
                int result = userFavoriteSongMapper.insert(userFavoriteSong);
                
                log.info("收藏歌曲成功: userId={}, songId={}", userId, songId);
                return result > 0;
                
            } else if ("unlike".equals(action)) {
                // 检查是否已经收藏
                Boolean isAlreadyFavorited = userFavoriteSongMapper.existsByUserIdAndSongId(userId, songId);
                boolean favorited = isAlreadyFavorited != null && isAlreadyFavorited;
                
                if (!favorited) {
                    log.info("歌曲未被收藏，无需取消: userId={}, songId={}", userId, songId);
                    return false;
                }
                
                // 取消收藏（逻辑删除）
                int result = userFavoriteSongMapper.deleteByUserIdAndSongId(userId, songId);
                
                log.info("取消收藏歌曲成功: userId={}, songId={}", userId, songId);
                return result > 0;
            }
            
            return false;
        } catch (Exception e) {
            log.error("收藏歌曲操作失败: userId={}, songId={}, action={}", userId, songId, action, e);
            throw new RuntimeException("收藏歌曲操作失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<SongResponse> getUserFavoriteSongs(Long userId, Integer page, Integer size) {
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 获取用户收藏的歌曲ID列表
            List<UserFavoriteSong> favoriteSongs = userFavoriteSongMapper.selectPageByUserId(userId, offset, size);
            
            // 获取总数
            Long total = userFavoriteSongMapper.countByUserId(userId);
            
            // 根据收藏记录获取歌曲详情
            List<SongResponse> songResponses = favoriteSongs.stream()
                    .map(favorite -> {
                        Song song = songMapper.selectById(favorite.getSongId());
                        if (song != null) {
                            return convertToSongResponse(song);
                        }
                        return null;
                    })
                    .filter(songResponse -> songResponse != null)
                    .collect(Collectors.toList());
            
            return new PageResult<>(total, songResponses, page, size);
        } catch (Exception e) {
            log.error("获取用户收藏歌曲失败: userId={}", userId, e);
            throw new RuntimeException("获取用户收藏歌曲失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isSongFavorited(Long userId, Long songId) {
        try {
            Boolean result = userFavoriteSongMapper.existsByUserIdAndSongId(userId, songId);
            return result != null && result;
        } catch (Exception e) {
            log.error("检查歌曲收藏状态失败: userId={}, songId={}", userId, songId, e);
            return false;
        }
    }
    
    @Override
    public java.util.Map<String, Object> getSongStreamUrl(Long songId) {
        try {
            Song song = songMapper.selectById(songId);
            if (song == null) {
                throw new RuntimeException("歌曲不存在");
            }
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            
            // 基本歌曲信息
            result.put("songId", song.getId());
            result.put("name", song.getName());
            result.put("duration", song.getDuration());
            
            // 音频URL - 支持多种字段名
            String audioUrl = song.getFilePath();
            if (audioUrl == null || audioUrl.trim().isEmpty()) {
                log.warn("歌曲音频文件路径为空: songId={}", songId);
                throw new RuntimeException("歌曲音频文件不存在");
            }
            
            // 如果是相对路径，转换为完整URL
            if (!audioUrl.startsWith("http")) {
                // 从配置中获取MinIO端点并构建完整URL
                if (audioUrl.startsWith("/")) {
                    audioUrl = audioUrl.substring(1);
                }
                // 这里假设fileUploadService可以提供URL转换
                try {
                    String objectName = extractObjectNameFromUrl(audioUrl);
                    if (objectName != null) {
                        audioUrl = fileUploadService.getPresignedUrl(objectName);
                    }
                } catch (Exception e) {
                    log.warn("无法生成预签名URL，使用原URL: {}", audioUrl);
                }
            }
            
            result.put("audioUrl", audioUrl);
            result.put("streamUrl", audioUrl); // 兼容性字段
            
            // 封面信息
            result.put("cover", song.getCover());
            result.put("albumCover", song.getCover()); // 兼容性字段
            
            // 艺术家信息
            if (song.getArtistId() != null) {
                Artist artist = artistMapper.selectById(song.getArtistId());
                if (artist != null) {
                    result.put("artist", artist.getName());
                    result.put("artistId", artist.getId());
                }
            }
            
            // 专辑信息
            if (song.getAlbumId() != null) {
                Album album = albumMapper.selectById(song.getAlbumId());
                if (album != null) {
                    result.put("albumName", album.getName());
                    result.put("albumId", album.getId());
                }
            }
            
            log.info("获取歌曲流媒体URL成功: songId={}, audioUrl={}", songId, audioUrl);
            return result;
            
        } catch (Exception e) {
            log.error("获取歌曲流媒体URL失败: songId={}", songId, e);
            throw new RuntimeException("获取音频流URL失败: " + e.getMessage());
        }
    }
    
    /**
     * 从URL中提取对象名称
     */
    private String extractObjectNameFromUrl(String url) {
        if (url == null) return null;
        
        try {
            // 如果包含bucket名称路径，提取对象名称
            if (url.contains("/chaoshi/")) {
                int index = url.indexOf("/chaoshi/");
                return url.substring(index + "/chaoshi/".length());
            }
            // 如果是简单的文件路径
            if (url.startsWith("audio/") || url.startsWith("image/")) {
                return url;
            }
            return null;
        } catch (Exception e) {
            log.error("提取对象名称失败: {}", url, e);
            return null;
        }
    }
    
    /**
     * 将Song实体转换为SongResponse
     */
    private SongResponse convertToSongResponse(Song song) {
        SongResponse response = new SongResponse();
        BeanUtils.copyProperties(song, response);
        
        // 获取专辑信息
        if (song.getAlbumId() != null) {
            Album album = albumMapper.selectById(song.getAlbumId());
            if (album != null) {
                response.setAlbumName(album.getName());
            }
        }
        
        // 获取歌手信息
        Artist artist = artistMapper.selectById(song.getArtistId());
        if (artist != null) {
            response.setArtistName(artist.getName());
        }
        
        return response;
    }
}