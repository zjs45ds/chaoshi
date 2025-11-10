package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.PageResult;
import com.chaoshi.dto.response.SongResponse;
import com.chaoshi.entity.Album;
import com.chaoshi.entity.Artist;
import com.chaoshi.entity.Song;
import com.chaoshi.entity.UserFavoriteSong;
import com.chaoshi.mapper.AlbumMapper;
import com.chaoshi.mapper.ArtistMapper;
import com.chaoshi.mapper.SongMapper;
import com.chaoshi.mapper.UserFavoriteSongMapper;
import com.chaoshi.service.SongService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 歌曲服务实现类（无文件上传功能）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
    
    private final SongMapper songMapper;
    private final UserFavoriteSongMapper userFavoriteSongMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    @Override
    @Transactional
    public Song createSong(Song song) {
        try {
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
    @Transactional
    public Song updateSong(Long id, Song song) {
        try {
            Song existingSong = getSongById(id);
            if (existingSong == null) {
                throw new RuntimeException("歌曲不存在");
            }
            
            song.setId(id);
            song.setUpdatedAt(LocalDateTime.now());
            
            int result = songMapper.updateSong(song);
            if (result > 0) {
                log.info("歌曲更新成功: {}", id);
                return getSongById(id);
            } else {
                throw new RuntimeException("歌曲更新失败");
            }
        } catch (Exception e) {
            log.error("更新歌曲失败: {}", id, e);
            throw new RuntimeException("更新歌曲失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteSong(Long id) {
        try {
            getSongById(id);
            
            songMapper.deleteSong(id);
            
            log.info("歌曲删除成功: {}", id);
        } catch (Exception e) {
            log.error("删除歌曲失败: {}", id, e);
            throw new RuntimeException("删除歌曲失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteSongs(List<Long> ids) {
        try {
            int result = songMapper.deleteBatch(ids);
            
            log.info("批量删除歌曲成功，数量: {}", result);
        } catch (Exception e) {
            log.error("批量删除歌曲失败", e);
            throw new RuntimeException("批量删除歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public Song getSongById(Long id) {
        try {
            Song song = songMapper.selectById(id);
            if (song == null) {
                throw new RuntimeException("歌曲不存在");
            }
            return song;
        } catch (Exception e) {
            log.error("获取歌曲失败: {}", id, e);
            throw new RuntimeException("获取歌曲失败: " + e.getMessage());
        }
    }


    private PageResult<Song> getSongsByPage(int page, int size) {
        try {
            int offset = (page - 1) * size;
            // 使用selectPage方法，传入null参数表示不过滤
            List<Song> songs = songMapper.selectPage(null, null, null, (long)offset, (long)size);
            Long total = songMapper.countSongs(null, null, null);
            
            return new PageResult<>(total, songs, page, size);
        } catch (Exception e) {
            log.error("分页获取歌曲失败", e);
            throw new RuntimeException("分页获取歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Song> searchSongs(String keyword, Integer page, Integer size) {
        try {
            int offset = (page - 1) * size;
            List<Song> songs = songMapper.searchSongs(keyword, (long)offset, (long)size);
            long total = songs.size();
            return new PageResult<>(total, songs, page, size);
        } catch (Exception e) {
            log.error("搜索歌曲失败: {}", keyword, e);
            throw new RuntimeException("搜索歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public List<Song> getSongsByArtist(Long artistId) {
        try {
            return songMapper.selectByArtistId(artistId);
        } catch (Exception e) {
            log.error("根据艺术家获取歌曲失败: {}", artistId, e);
            throw new RuntimeException("根据艺术家获取歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public List<Song> getSongsByAlbum(Long albumId) {
        try {
            return songMapper.selectByAlbumId(albumId);
        } catch (Exception e) {
            log.error("根据专辑获取歌曲失败: {}", albumId, e);
            throw new RuntimeException("根据专辑获取歌曲失败: " + e.getMessage());
        }
    }


    @Transactional
    protected void addToFavorites(Long userId, Long songId) {
        try {
            // 检查歌曲是否存在
            Song song = songMapper.selectById(songId);
            if (song == null) {
                throw new RuntimeException("歌曲不存在");
            }
            
            // 检查是否已经收藏
            Boolean exists = userFavoriteSongMapper.existsByUserIdAndSongId(userId, songId);
            if (exists != null && exists) {
                throw new RuntimeException("歌曲已经在收藏夹中");
            }
            
            UserFavoriteSong favorite = new UserFavoriteSong();
            favorite.setUserId(userId);
            favorite.setSongId(songId);
            favorite.setCreatedAt(LocalDateTime.now());
            
            userFavoriteSongMapper.insert(favorite);
            log.info("添加收藏成功: userId={}, songId={}", userId, songId);
        } catch (Exception e) {
            log.error("添加收藏失败: userId={}, songId={}", userId, songId, e);
            throw new RuntimeException("添加收藏失败: " + e.getMessage());
        }
    }

    // 内部辅助方法，用于支持其他方法
    @Transactional
    protected void removeFromFavorites(Long userId, Long songId) {
        try {
            userFavoriteSongMapper.deleteByUserIdAndSongId(userId, songId);
            log.info("取消收藏成功: userId={}, songId={}", userId, songId);
        } catch (Exception e) {
            log.error("取消收藏失败: userId={}, songId={}", userId, songId, e);
            throw new RuntimeException("取消收藏失败: " + e.getMessage());
        }
    }

    // 内部辅助方法，用于支持其他方法
    private List<Song> getFavoritesByUserId(Long userId) {
        try {
            List<UserFavoriteSong> favorites = userFavoriteSongMapper.selectByUserId(userId);
            List<Song> songs = new ArrayList<>();
            for (UserFavoriteSong favorite : favorites) {
                Song song = songMapper.selectById(favorite.getSongId());
                if (song != null) {
                    songs.add(song);
                }
            }
            return songs;
        } catch (Exception e) {
            log.error("获取用户收藏歌曲失败: {}", userId, e);
            throw new RuntimeException("获取用户收藏歌曲失败: " + e.getMessage());
        }
    }

    // 内部辅助方法，用于支持其他方法
    private boolean isFavorite(Long userId, Long songId) {
        try {
            Boolean exists = userFavoriteSongMapper.existsByUserIdAndSongId(userId, songId);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("检查收藏状态失败: userId={}, songId={}", userId, songId, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getSongStreamUrl(Long songId) {
        try {
            Song song = getSongById(songId);
            String audioUrl = song.getFilePath();
            
            if (audioUrl == null || audioUrl.isEmpty()) {
                throw new RuntimeException("歌曲文件路径为空");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", audioUrl);
            result.put("songId", songId);
            result.put("title", song.getName());
            
            log.info("获取歌曲播放URL成功: songId={}, url={}", songId, audioUrl);
            return result;
        } catch (Exception e) {
            log.error("获取歌曲播放URL失败: {}", songId, e);
            throw new RuntimeException("获取歌曲播放URL失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Song> getSongPage(String name, Long artistId, Long albumId, Integer page, Integer size) {
        try {
            int offset = (page - 1) * size;
            List<Song> songs = songMapper.selectPage(name, artistId, albumId, (long)offset, (long)size);
            Long total = songMapper.countSongs(name, artistId, albumId);
            
            return new PageResult<>(total, songs, page, size);
        } catch (Exception e) {
            log.error("分页获取歌曲失败", e);
            throw new RuntimeException("分页获取歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public List<Song> getSongsByArtistId(Long artistId) {
        return getSongsByArtist(artistId);
    }

    @Override
    public List<Song> getSongsByAlbumId(Long albumId) {
        return getSongsByAlbum(albumId);
    }

    @Override
    public PageResult<SongResponse> getSongList(Integer page, Integer size) {
        try {
            PageResult<Song> songPage = getSongsByPage(page, size);
            List<SongResponse> songResponses = new ArrayList<>();
            
            for (Song song : songPage.getContent()) {
                SongResponse response = new SongResponse();
                BeanUtils.copyProperties(song, response);
                
                // 获取歌手名称
                try {
                    Artist artist = artistMapper.selectById(song.getArtistId());
                    response.setArtistName(artist != null ? artist.getName() : "未知歌手");
                } catch (Exception e) {
                    log.warn("获取歌手信息失败: artistId={}", song.getArtistId(), e);
                    response.setArtistName("未知歌手");
                }
                
                // 获取专辑名称
                if (song.getAlbumId() != null) {
                    try {
                        Album album = albumMapper.selectById(song.getAlbumId());
                        response.setAlbumName(album != null ? album.getName() : "未知专辑");
                    } catch (Exception e) {
                        log.warn("获取专辑信息失败: albumId={}", song.getAlbumId(), e);
                        response.setAlbumName("未知专辑");
                    }
                } else {
                    response.setAlbumName("未知专辑");
                }
                
                songResponses.add(response);
            }
            
            return new PageResult<>(songPage.getTotal(), songResponses, page, size);
        } catch (Exception e) {
            log.error("获取歌曲列表失败", e);
            throw new RuntimeException("获取歌曲列表失败: " + e.getMessage());
        }
    }

    @Override
    public boolean favoriteSong(Long userId, Long songId, String action) {
        try {
            if ("like".equals(action)) {
                addToFavorites(userId, songId);
                return true;
            } else if ("unlike".equals(action)) {
                removeFromFavorites(userId, songId);
                return false;
            }
            throw new RuntimeException("无效的操作类型: " + action);
        } catch (Exception e) {
            log.error("收藏操作失败: userId={}, songId={}, action={}", userId, songId, action, e);
            throw new RuntimeException("收藏操作失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<SongResponse> getUserFavoriteSongs(Long userId, Integer page, Integer size) {
        try {
            List<Song> favoriteSongs = getFavoritesByUserId(userId);
            
            // 简单分页处理
            int offset = (page - 1) * size;
            List<Song> pagedSongs = favoriteSongs.stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());
            
            List<SongResponse> songResponses = new ArrayList<>();
            for (Song song : pagedSongs) {
                SongResponse response = new SongResponse();
                BeanUtils.copyProperties(song, response);
                
                // 获取歌手名称
                try {
                    Artist artist = artistMapper.selectById(song.getArtistId());
                    response.setArtistName(artist != null ? artist.getName() : "未知歌手");
                } catch (Exception e) {
                    log.warn("获取歌手信息失败: artistId={}", song.getArtistId(), e);
                    response.setArtistName("未知歌手");
                }
                
                // 获取专辑名称
                if (song.getAlbumId() != null) {
                    try {
                        Album album = albumMapper.selectById(song.getAlbumId());
                        response.setAlbumName(album != null ? album.getName() : "未知专辑");
                    } catch (Exception e) {
                        log.warn("获取专辑信息失败: albumId={}", song.getAlbumId(), e);
                        response.setAlbumName("未知专辑");
                    }
                } else {
                    response.setAlbumName("未知专辑");
                }
                
                songResponses.add(response);
            }
            
            return new PageResult<>((long)favoriteSongs.size(), songResponses, page, size);
        } catch (Exception e) {
            log.error("获取用户收藏歌曲失败: {}", userId, e);
            throw new RuntimeException("获取用户收藏歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isSongFavorited(Long userId, Long songId) {
        return isFavorite(userId, songId);
    }
}
