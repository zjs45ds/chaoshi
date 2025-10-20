package org.example.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.ApiResult;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Album;
import org.example.chaoshi.entity.Artist;
import org.example.chaoshi.entity.Song;
import org.example.chaoshi.entity.Toplist;
import org.example.chaoshi.entity.ToplistSong;
import org.example.chaoshi.service.AlbumService;
import org.example.chaoshi.service.ArtistService;
import org.example.chaoshi.service.ToplistService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排行榜控制器
 */
@RestController
@RequestMapping("/api/toplists")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "排行榜管理", description = "排行榜的增删改查操作")
@Validated
@CrossOrigin(origins = "*")
public class ToplistController {
    
    private final ToplistService toplistService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    
    @PostMapping
    @Operation(summary = "创建排行榜", description = "创建新的排行榜")
    public ApiResult<Toplist> createToplist(
            @Parameter(description = "排行榜信息") @Valid @RequestBody Toplist toplist) {
        
        try {
            Toplist result = toplistService.createToplist(toplist);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("创建排行榜失败", e);
            return ApiResult.error("创建排行榜失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取排行榜详情", description = "根据ID获取排行榜详细信息")
    public ApiResult<Toplist> getToplist(
            @Parameter(description = "排行榜ID") @PathVariable Long id) {
        
        try {
            Toplist toplist = toplistService.getToplistById(id);
            return ApiResult.success(toplist);
        } catch (Exception e) {
            log.error("获取排行榜失败: {}", id, e);
            return ApiResult.error("获取排行榜失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新排行榜", description = "更新排行榜信息")
    public ApiResult<Toplist> updateToplist(
            @Parameter(description = "排行榜ID") @PathVariable Long id,
            @Parameter(description = "排行榜信息") @Valid @RequestBody Toplist toplist) {
        
        try {
            Toplist result = toplistService.updateToplist(id, toplist);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("更新排行榜失败: {}", id, e);
            return ApiResult.error("更新排行榜失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除排行榜", description = "根据ID删除排行榜")
    public ApiResult<Boolean> deleteToplist(
            @Parameter(description = "排行榜ID") @PathVariable Long id) {
        
        try {
            boolean result = toplistService.deleteToplist(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("删除排行榜失败: {}", id, e);
            return ApiResult.error("删除排行榜失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除排行榜", description = "根据ID列表批量删除排行榜")
    public ApiResult<Boolean> deleteToplists(
            @Parameter(description = "排行榜ID列表") @RequestBody List<Long> ids) {
        
        try {
            boolean result = toplistService.deleteToplists(ids);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("批量删除排行榜失败", e);
            return ApiResult.error("批量删除排行榜失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "分页获取排行榜列表", description = "支持按名称筛选")
    public ApiResult<PageResult<Toplist>> getToplistPage(
            @Parameter(description = "排行榜名称") @RequestParam(required = false) String name,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Toplist> result = toplistService.getToplistPage(name, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取排行榜列表失败", e);
            return ApiResult.error("获取排行榜列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "获取所有排行榜", description = "获取所有排行榜列表")
    public ApiResult<List<Toplist>> getAllToplists() {
        
        try {
            List<Toplist> toplists = toplistService.getAllToplists();
            return ApiResult.success(toplists);
        } catch (Exception e) {
            log.error("获取所有排行榜失败", e);
            return ApiResult.error("获取所有排行榜失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索排行榜", description = "根据关键词搜索排行榜")
    public ApiResult<PageResult<Toplist>> searchToplists(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Toplist> result = toplistService.searchToplists(keyword, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("搜索排行榜失败", e);
            return ApiResult.error("搜索排行榜失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/hot")
    @Operation(summary = "获取热门排行榜", description = "获取热门排行榜列表")
    public ApiResult<List<Toplist>> getHotToplists(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Toplist> toplists = toplistService.getHotToplists(limit);
            return ApiResult.success(toplists);
        } catch (Exception e) {
            log.error("获取热门排行榜失败", e);
            return ApiResult.error("获取热门排行榜失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/latest")
    @Operation(summary = "获取最新排行榜", description = "获取最新创建的排行榜列表")
    public ApiResult<List<Toplist>> getLatestToplists(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Toplist> toplists = toplistService.getLatestToplists(limit);
            return ApiResult.success(toplists);
        } catch (Exception e) {
            log.error("获取最新排行榜失败", e);
            return ApiResult.error("获取最新排行榜失败: " + e.getMessage());
        }
    }
    
    // ==================== 排行榜歌曲相关接口 ====================
    
    @GetMapping("/{id}/songs")
    @Operation(summary = "获取排行榜歌曲列表", description = "分页获取排行榜中的歌曲")
    public ApiResult<PageResult<Map<String, Object>>> getToplistSongs(
            @Parameter(description = "排行榜ID") @PathVariable Long id,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "50") @RequestParam(defaultValue = "50") Integer size) {
        
        try {
            PageResult<Song> result = toplistService.getToplistSongs(id, page, size);
            
            // 转换歌曲数据，添加歌手名和专辑名
            List<Map<String, Object>> enhancedSongs = result.getContent().stream()
                .map(song -> {
                    Map<String, Object> songMap = new HashMap<>();
                    songMap.put("id", song.getId());
                    songMap.put("name", song.getName());
                    songMap.put("artistId", song.getArtistId());
                    songMap.put("albumId", song.getAlbumId());
                    songMap.put("duration", song.getDuration());
                    songMap.put("filePath", song.getFilePath());
                    songMap.put("lyrics", song.getLyrics());
                    songMap.put("cover", song.getCover());
                    songMap.put("createdAt", song.getCreatedAt());
                    songMap.put("updatedAt", song.getUpdatedAt());
                    
                    // 获取歌手名称
                    try {
                        Artist artist = artistService.getArtistById(song.getArtistId());
                        songMap.put("artistName", artist != null ? artist.getName() : "未知歌手");
                        songMap.put("artist", artist != null ? artist.getName() : "未知歌手");
                    } catch (Exception e) {
                        log.warn("获取歌手信息失败: artistId={}", song.getArtistId(), e);
                        songMap.put("artistName", "未知歌手");
                        songMap.put("artist", "未知歌手");
                    }
                    
                    // 获取专辑名称
                    if (song.getAlbumId() != null) {
                        try {
                            Album album = albumService.getAlbumById(song.getAlbumId());
                            songMap.put("albumName", album != null ? album.getName() : "未知专辑");
                            songMap.put("album", album != null ? album.getName() : "未知专辑");
                        } catch (Exception e) {
                            log.warn("获取专辑信息失败: albumId={}", song.getAlbumId(), e);
                            songMap.put("albumName", "未知专辑");
                            songMap.put("album", "未知专辑");
                        }
                    } else {
                        songMap.put("albumName", "未知专辑");
                        songMap.put("album", "未知专辑");
                    }
                    
                    return songMap;
                })
                .collect(Collectors.toList());
            
            PageResult<Map<String, Object>> enhancedResult = new PageResult<>(
                result.getTotal(), 
                enhancedSongs, 
                result.getPage(), 
                result.getSize()
            );
            
            return ApiResult.success(enhancedResult);
        } catch (Exception e) {
            log.error("获取排行榜歌曲列表失败: {}", id, e);
            return ApiResult.error("获取排行榜歌曲列表失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/songs/{songId}")
    @Operation(summary = "添加歌曲到排行榜", description = "将歌曲添加到指定排行榜")
    public ApiResult<Boolean> addSongToToplist(
            @Parameter(description = "排行榜ID") @PathVariable Long id,
            @Parameter(description = "歌曲ID") @PathVariable Long songId,
            @Parameter(description = "排名") @RequestParam Integer rank) {
        
        try {
            boolean result = toplistService.addSongToToplist(id, songId, rank);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("添加歌曲到排行榜失败: 排行榜ID={}, 歌曲ID={}", id, songId, e);
            return ApiResult.error("添加歌曲到排行榜失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/songs/batch")
    @Operation(summary = "批量添加歌曲到排行榜", description = "批量将歌曲添加到指定排行榜")
    public ApiResult<Boolean> addSongsToToplist(
            @Parameter(description = "排行榜ID") @PathVariable Long id,
            @Parameter(description = "排行榜歌曲关联列表") @Valid @RequestBody List<ToplistSong> toplistSongs) {
        
        try {
            boolean result = toplistService.addSongsToToplist(id, toplistSongs);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("批量添加歌曲到排行榜失败: {}", id, e);
            return ApiResult.error("批量添加歌曲到排行榜失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}/songs/{songId}")
    @Operation(summary = "从排行榜移除歌曲", description = "从指定排行榜中移除歌曲")
    public ApiResult<Boolean> removeSongFromToplist(
            @Parameter(description = "排行榜ID") @PathVariable Long id,
            @Parameter(description = "歌曲ID") @PathVariable Long songId) {
        
        try {
            boolean result = toplistService.removeSongFromToplist(id, songId);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("从排行榜移除歌曲失败: 排行榜ID={}, 歌曲ID={}", id, songId, e);
            return ApiResult.error("从排行榜移除歌曲失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/songs/{songId}/rank")
    @Operation(summary = "更新排行榜歌曲排名", description = "更新指定歌曲在排行榜中的排名")
    public ApiResult<Boolean> updateSongRank(
            @Parameter(description = "排行榜ID") @PathVariable Long id,
            @Parameter(description = "歌曲ID") @PathVariable Long songId,
            @Parameter(description = "新排名") @RequestParam Integer newRank) {
        
        try {
            boolean result = toplistService.updateSongRank(id, songId, newRank);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("更新排行榜歌曲排名失败: 排行榜ID={}, 歌曲ID={}, 新排名={}", id, songId, newRank, e);
            return ApiResult.error("更新排行榜歌曲排名失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}/songs")
    @Operation(summary = "清空排行榜歌曲", description = "清空指定排行榜中的所有歌曲")
    public ApiResult<Boolean> clearToplistSongs(
            @Parameter(description = "排行榜ID") @PathVariable Long id) {
        
        try {
            boolean result = toplistService.clearToplistSongs(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("清空排行榜歌曲失败: {}", id, e);
            return ApiResult.error("清空排行榜歌曲失败: " + e.getMessage());
        }
    }
}