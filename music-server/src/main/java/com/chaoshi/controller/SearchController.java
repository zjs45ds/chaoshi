package com.chaoshi.controller;

import com.chaoshi.entity.*;
import com.chaoshi.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.chaoshi.dto.ApiResult;
import com.chaoshi.dto.PageResult;
import com.chaoshi.service.SearchHistoryService;
import org.springframework.web.bind.annotation.*; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索Controller
 */
@Tag(name = "搜索功能", description = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SearchController {
    
    private final SongMapper songMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;
    private final MvMapper mvMapper;
    private final SearchHistoryService searchHistoryService;
    
    @Operation(summary = "综合搜索", description = "根据关键词搜索歌曲、专辑、歌手、播放列表等")
    @GetMapping("")
    public ApiResult<PageResult<Map<String, Object>>> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            // 这里暂时返回模拟数据
            List<Map<String, Object>> results = new ArrayList<>();
            
            // 模拟搜索结果
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("name", "演员");
            result1.put("artist_name", "薛之谦");
            result1.put("type", "song");
            result1.put("cover", "https://example.com/cover1.jpg");
            results.add(result1);
            
            PageResult<Map<String, Object>> pageResult = PageResult.of(1L, results, page, size);
            return ApiResult.success(pageResult);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "获取搜索历史", description = "获取用户搜索历史")
    @GetMapping("/history")
    public ApiResult<List<String>> getSearchHistory(@RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                return ApiResult.success(new ArrayList<>());
            }
            
            List<SearchHistory> searchHistories = searchHistoryService.getUserSearchHistory(userId, 20);
            List<String> history = searchHistories.stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
            
            return ApiResult.success(history);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "保存搜索历史", description = "保存用户搜索关键词到历史记录")
    @PostMapping("/history")
    public ApiResult<String> saveSearchHistory(@RequestBody Map<String, Object> request) {
        try {
            String keyword = (String) request.get("keyword");
            Long userId = null;
            
            // 尝试从请求中获取用户ID
            Object userIdObj = request.get("userId");
            if (userIdObj != null) {
                if (userIdObj instanceof Number) {
                    userId = ((Number) userIdObj).longValue();
                } else if (userIdObj instanceof String) {
                    try {
                        userId = Long.parseLong((String) userIdObj);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            
            if (userId != null && keyword != null && !keyword.trim().isEmpty()) {
                searchHistoryService.saveSearchHistory(userId, keyword.trim());
            return ApiResult.success("搜索历史保存成功");
            } else {
                return ApiResult.error("用户ID或关键词不能为空");
            }
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "删除单个搜索历史", description = "删除指定的搜索历史项")
    @DeleteMapping("/history/{keyword}")
    public ApiResult<String> deleteSearchHistoryItem(
            @PathVariable String keyword, 
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                return ApiResult.error("用户ID不能为空");
            }
            
            searchHistoryService.deleteSearchHistoryItem(userId, keyword);
            return ApiResult.success("搜索历史项已删除");
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "清空搜索历史", description = "清空用户所有搜索历史")
    @DeleteMapping("/history")
    public ApiResult<String> clearSearchHistory(@RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                return ApiResult.error("用户ID不能为空");
            }
            
            searchHistoryService.clearUserSearchHistory(userId);
            return ApiResult.success("搜索历史已清空");
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "综合搜索", description = "搜索所有类型的内容")
    @GetMapping("/all")
    public ApiResult<Map<String, Object>> searchAll(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 保存搜索历史（如果提供了用户ID）
            if (userId != null && keyword != null && !keyword.trim().isEmpty()) {
                searchHistoryService.saveSearchHistory(userId, keyword.trim());
            }
            
            long offset = (long) page * size;
            
            // 从数据库搜索歌曲
            List<Song> songEntities = songMapper.searchSongs(keyword, offset, 5L);
            List<Map<String, Object>> songs = songEntities.stream()
                .map(this::convertSongToMap)
                .collect(Collectors.toList());
            
            // 从数据库搜索歌手
            List<Artist> artistEntities = artistMapper.searchArtists(keyword, offset, 3L);
            List<Map<String, Object>> artists = artistEntities.stream()
                .map(this::convertArtistToMap)
                .collect(Collectors.toList());
            
            // 从数据库搜索专辑
            List<Album> albumEntities = albumMapper.searchAlbums(keyword, offset, 3L);
            List<Map<String, Object>> albums = albumEntities.stream()
                .map(this::convertAlbumToMap)
                .collect(Collectors.toList());
            
            // 从数据库搜索歌单
            List<Playlist> playlistEntities = playlistMapper.searchPlaylists(keyword, offset, 3L);
            List<Map<String, Object>> playlists = playlistEntities.stream()
                .map(this::convertPlaylistToMap)
                .collect(Collectors.toList());
            
            // 从数据库搜索MV
            List<Mv> mvEntities = mvMapper.searchMvs(keyword, offset, 2L);
            List<Map<String, Object>> mvs = mvEntities.stream()
                .map(this::convertMvToMap)
                .collect(Collectors.toList());
            
            result.put("songs", songs);
            result.put("artists", artists);
            result.put("albums", albums);
            result.put("playlists", playlists);
            result.put("mvs", mvs);
            
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @Operation(summary = "搜索建议", description = "根据关键词提供搜索建议")
    @GetMapping("/suggest")
    public ApiResult<List<String>> searchSuggest(@RequestParam String keyword) {
        try {
            List<String> suggestions = new ArrayList<>();

            // 模拟搜索建议
            if (keyword.contains("薛")) {
                suggestions.add("薛之谦");
                suggestions.add("薛之谦 演员");
                suggestions.add("薛之谦 天外来物");
            } else if (keyword.contains("周")) {
                suggestions.add("周杰伦");
                suggestions.add("周杰伦 稻香");
                suggestions.add("周杰伦 青花瓷");
            } else {
                suggestions.add(keyword + " 相关搜索1");
                suggestions.add(keyword + " 相关搜索2");
                suggestions.add(keyword + " 相关搜索3");
            }

            return ApiResult.success(suggestions);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @Operation(summary = "模糊搜索", description = "支持拼音、首字母、部分匹配的搜索")
    @GetMapping("/fuzzy")
    public ApiResult<PageResult<Map<String, Object>>> fuzzySearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<Map<String, Object>> results = new ArrayList<>();
            
            // 模拟模糊搜索结果
            Map<String, Object> result1 = new HashMap<>();
            result1.put("id", 1L);
            result1.put("name", "演员");
            result1.put("artist_name", "薛之谦");
            result1.put("type", "song");
            results.add(result1);
            
            PageResult<Map<String, Object>> pageResult = PageResult.of(1L, results, page, size);
            return ApiResult.success(pageResult);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "智能搜索建议", description = "实时搜索建议")
    @GetMapping("/smart-suggest")
    public ApiResult<List<Map<String, Object>>> smartSuggest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> suggestions = new ArrayList<>();
            
            // 模拟智能建议
            Map<String, Object> suggestion1 = new HashMap<>();
            suggestion1.put("keyword", keyword);
            suggestion1.put("type", "song");
            suggestion1.put("count", 100);
            suggestions.add(suggestion1);
            
            return ApiResult.success(suggestions);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    // 实体转换为Map的方法
    private Map<String, Object> convertSongToMap(Song song) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", song.getId());
        map.put("name", song.getName());
        map.put("artistId", song.getArtistId());
        map.put("albumId", song.getAlbumId());
        map.put("duration", song.getDuration());
        map.put("cover", song.getCover());
        map.put("filePath", song.getFilePath());
        // 需要通过关联查询获取艺术家和专辑名称，这里暂时使用ID
        return map;
    }
    
    private Map<String, Object> convertArtistToMap(Artist artist) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", artist.getId());
        map.put("name", artist.getName());
        map.put("description", artist.getDescription());
        map.put("avatar", artist.getAvatar());
        return map;
    }
    
    private Map<String, Object> convertAlbumToMap(Album album) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", album.getId());
        map.put("name", album.getName());
        map.put("artistId", album.getArtistId());
        // 添加默认封面支持
        map.put("cover", album.getCover() != null && !album.getCover().isEmpty() ? album.getCover() : "/src/assets/1音乐.png");
        map.put("releaseDate", album.getReleaseDate());
        // 需要通过关联查询获取艺术家名称，这里暂时使用ID
        return map;
    }
    
    private Map<String, Object> convertPlaylistToMap(Playlist playlist) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", playlist.getId());
        map.put("name", playlist.getName());
        map.put("description", playlist.getDescription());
        map.put("coverUrl", playlist.getCoverUrl());
        map.put("songCount", playlist.getSongCount());
        return map;
    }
    
    private Map<String, Object> convertMvToMap(Mv mv) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", mv.getId());
        map.put("name", mv.getName());
        map.put("artistId", mv.getArtistId());
        map.put("cover", mv.getCover());
        map.put("videoPath", mv.getVideoPath());
        map.put("artist", mv.getArtist()); // 这个是@Transient字段
        return map;
    }
}