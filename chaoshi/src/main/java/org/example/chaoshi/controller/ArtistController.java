package org.example.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.chaoshi.dto.ApiResult;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.dto.response.AlbumResponse;
import org.example.chaoshi.dto.response.SongResponse;
import org.example.chaoshi.entity.Artist;
import org.example.chaoshi.entity.ArtistTimeline;
import org.example.chaoshi.service.ArtistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 歌手Controller
 */
@Tag(name = "歌手管理", description = "歌手相关接口")
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArtistController {
    
    private final ArtistService artistService;
    
    @GetMapping
    @Operation(summary = "分页获取歌手列表", description = "支持按名称筛选")
    public ApiResult<PageResult<Artist>> getArtistPage(
            @Parameter(description = "歌手名称") @RequestParam(required = false) String name,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Artist> result = artistService.getArtistPage(name, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("获取歌手列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取歌手详情", description = "根据ID获取歌手详细信息")
    public ApiResult<Artist> getArtist(
            @Parameter(description = "歌手ID") @PathVariable Long id) {
        
        try {
            Artist artist = artistService.getArtistById(id);
            return ApiResult.success(artist);
        } catch (Exception e) {
            return ApiResult.error("获取歌手失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/hot")
    @Operation(summary = "获取热门歌手", description = "获取热门歌手列表")
    public ApiResult<List<Artist>> getHotArtists(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Artist> artists = artistService.getPopularArtists(limit);
            return ApiResult.success(artists);
        } catch (Exception e) {
            return ApiResult.error("获取热门歌手失败: " + e.getMessage());
        }
    }
    
    // ===== 薛之谦相关接口 =====
    
    @Operation(summary = "获取薛之谦专辑数据", description = "获取薛之谦的专辑列表")
    @GetMapping("/xuezhiqian/albums")
    public ApiResult<List<AlbumResponse>> getXueZhiQianAlbums() {
        try {
            List<AlbumResponse> albums = artistService.getXueZhiQianAlbums();
            return ApiResult.success(albums);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "获取薛之谦时间线", description = "获取薛之谦的职业生涯时间线")
    @GetMapping("/xuezhiqian/timeline")
    public ApiResult<List<ArtistTimeline>> getXueZhiQianTimeline() {
        try {
            List<ArtistTimeline> timeline = artistService.getXueZhiQianTimeline();
            return ApiResult.success(timeline);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "获取薛之谦热门歌曲", description = "获取薛之谦的热门歌曲列表")
    @GetMapping("/xuezhiqian/hotsongs")
    public ApiResult<List<SongResponse>> getXueZhiQianHotSongs() {
        try {
            List<SongResponse> songs = artistService.getXueZhiQianHotSongs();
            return ApiResult.success(songs);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "获取薛之谦统计数据", description = "获取薛之谦的实时统计数据")
    @GetMapping("/xuezhiqian/stats")
    public ApiResult<Map<String, Object>> getXueZhiQianStats() {
        try {
            Map<String, Object> stats = artistService.getXueZhiQianStats();
            return ApiResult.success(stats);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
}