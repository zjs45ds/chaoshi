package org.example.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.chaoshi.dto.ApiResult;
import org.example.chaoshi.dto.PageResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索Controller
 */
@Tag(name = "搜索功能", description = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SearchController {
    
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
    
    @Operation(summary = "获取热门搜索", description = "获取热门搜索关键词列表")
    @GetMapping("/hot")
    public ApiResult<List<Map<String, Object>>> getHotSearch() {
        try {
            List<Map<String, Object>> hotSearches = new ArrayList<>();
            
            // 模拟热门搜索数据
            String[] hotKeywords = {"薛之谦", "周杰伦", "林俊杰", "邓紫棋", "张学友", "陈奕迅", "演员", "稻香", "华语流行", "经典老歌"};
            String[] types = {"artist", "artist", "artist", "artist", "artist", "artist", "song", "song", "playlist", "playlist"};
            
            for (int i = 0; i < hotKeywords.length; i++) {
                Map<String, Object> hotItem = new HashMap<>();
                hotItem.put("id", (long) (i + 1));
                hotItem.put("keyword", hotKeywords[i]);
                hotItem.put("name", hotKeywords[i]);
                hotItem.put("type", types[i]);
                hotItem.put("hot", i < 3); // 前3个标记为热门
                hotItem.put("searchCount", 10000 - i * 1000); // 模拟搜索次数
                hotSearches.add(hotItem);
            }
            
            return ApiResult.success(hotSearches);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "获取搜索历史", description = "获取用户搜索历史")
    @GetMapping("/history")
    public ApiResult<List<String>> getSearchHistory() {
        try {
            // 模拟搜索历史数据
            List<String> history = new ArrayList<>();
            history.add("薛之谦");
            history.add("演员");
            history.add("周杰伦");
            history.add("稻香");
            history.add("林俊杰");
            
            return ApiResult.success(history);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "保存搜索历史", description = "保存用户搜索关键词到历史记录")
    @PostMapping("/history")
    public ApiResult<String> saveSearchHistory(@RequestBody Map<String, String> request) {
        try {
            String keyword = request.get("keyword");
            // 这里应该保存到数据库，暂时只返回成功
            return ApiResult.success("搜索历史保存成功");
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "清空搜索历史", description = "清空用户所有搜索历史")
    @DeleteMapping("/history")
    public ApiResult<String> clearSearchHistory() {
        try {
            // 这里应该从数据库删除，暂时只返回成功
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
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 模拟歌曲搜索结果
            List<Map<String, Object>> songs = new ArrayList<>();
            Map<String, Object> song1 = new HashMap<>();
            song1.put("id", 1L);
            song1.put("name", "演员");
            song1.put("artistName", "薛之谦");
            song1.put("duration", 240);
            songs.add(song1);
            
            // 模拟歌手搜索结果
            List<Map<String, Object>> artists = new ArrayList<>();
            Map<String, Object> artist1 = new HashMap<>();
            artist1.put("id", 1L);
            artist1.put("name", "薛之谦");
            artist1.put("description", "华语流行歌手");
            artists.add(artist1);
            
            // 模拟专辑搜索结果
            List<Map<String, Object>> albums = new ArrayList<>();
            Map<String, Object> album1 = new HashMap<>();
            album1.put("id", 1L);
            album1.put("name", "天外来物");
            album1.put("artistName", "薛之谦");
            albums.add(album1);
            
            // 模拟歌单搜索结果
            List<Map<String, Object>> playlists = new ArrayList<>();
            Map<String, Object> playlist1 = new HashMap<>();
            playlist1.put("id", 1L);
            playlist1.put("name", "华语流行");
            playlist1.put("description", "1000万播放");
            playlist1.put("trackCount", 50);
            playlists.add(playlist1);
            
            // 模拟MV搜索结果
            List<Map<String, Object>> mvs = new ArrayList<>();
            Map<String, Object> mv1 = new HashMap<>();
            mv1.put("id", 1L);
            mv1.put("name", "演员MV");
            mv1.put("artistName", "薛之谦");
            mvs.add(mv1);
            
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
    
    @Operation(summary = "获取热门歌手", description = "获取热门歌手列表")
    @GetMapping("/hot-artists")
    public ApiResult<List<Map<String, Object>>> getHotArtists(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> hotArtists = new ArrayList<>();
            
            String[] artistNames = {"薛之谦", "周杰伦", "林俊杰", "邓紫棋", "张学友", "陈奕迅", "王菲", "刘德华", "张信哲", "梁静茹"};
            
            for (int i = 0; i < Math.min(limit, artistNames.length); i++) {
                Map<String, Object> artist = new HashMap<>();
                artist.put("id", (long) (i + 1));
                artist.put("name", artistNames[i]);
                artist.put("description", "华语流行歌手");
                artist.put("fanCount", 1000000 - i * 50000);
                hotArtists.add(artist);
            }
            
            return ApiResult.success(hotArtists);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "获取热门歌曲", description = "获取热门歌曲列表")
    @GetMapping("/hot-songs")
    public ApiResult<List<Map<String, Object>>> getHotSongs(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> hotSongs = new ArrayList<>();
            
            String[] songNames = {"演员", "稻香", "青花瓷", "光年之外", "吻别", "十年", "传奇", "冰雨", "爱如潮水", "月亮代表我的心"};
            String[] artistNames = {"薛之谦", "周杰伦", "周杰伦", "邓紫棋", "张学友", "陈奕迅", "王菲", "刘德华", "张信哲", "邓丽君"};
            
            for (int i = 0; i < Math.min(limit, songNames.length); i++) {
                Map<String, Object> song = new HashMap<>();
                song.put("id", (long) (i + 1));
                song.put("name", songNames[i]);
                song.put("artistName", artistNames[i]);
                song.put("duration", 240 + i * 10);
                song.put("playCount", 10000000 - i * 500000);
                hotSongs.add(song);
            }
            
            return ApiResult.success(hotSongs);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
}