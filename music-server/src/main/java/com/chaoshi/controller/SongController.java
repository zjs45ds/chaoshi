package com.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.ApiResult;
import com.chaoshi.dto.PageResult;
import com.chaoshi.dto.response.SongResponse;
import com.chaoshi.entity.Song;
import com.chaoshi.service.SongService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 歌曲Controller
 */
@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class SongController {
    
    private final SongService songService;
    
    @PostMapping
    public ApiResult<Song> createSong(@Parameter(description = "歌曲信息") @Valid @RequestBody Song song) {
        try {
            Song result = songService.createSong(song);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("创建歌曲失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取歌曲详情", description = "根据ID获取歌曲详细信息")
    public ApiResult<Song> getSong(
            @Parameter(description = "歌曲ID") @PathVariable Long id) {
        
        try {
            Song song = songService.getSongById(id);
            return ApiResult.success(song);
        } catch (Exception e) {
            return ApiResult.error("获取歌曲失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/stream-url")
    public ApiResult<Map<String, Object>> getSongStreamUrl(
            @Parameter(description = "歌曲ID") @PathVariable Long id) {
        
        try {
            Map<String, Object> result = songService.getSongStreamUrl(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("获取音频流URL失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新歌曲", description = "更新歌曲信息")
    public ApiResult<Song> updateSong(
            @Parameter(description = "歌曲ID") @PathVariable Long id,
            @Parameter(description = "歌曲信息") @Valid @RequestBody Song song) {
        try {
            Song result = songService.updateSong(id, song);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("更新歌曲失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除歌曲", description = "根据ID删除歌曲")
    public ApiResult<Boolean> deleteSong(
            @Parameter(description = "歌曲ID") @PathVariable Long id) {
        try {
            songService.deleteSong(id);
            return ApiResult.success(true);
        } catch (Exception e) {
            return ApiResult.error("删除歌曲失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除歌曲", description = "根据ID列表批量删除歌曲")
    public ApiResult<Boolean> deleteSongs(
            @Parameter(description = "歌曲ID列表") @RequestBody List<Long> ids) {
        try {
            songService.deleteSongs(ids);
            return ApiResult.success(true);
        } catch (Exception e) {
            return ApiResult.error("批量删除歌曲失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "分页获取歌曲列表", description = "支持按名称、歌手、专辑筛选")
    public ApiResult<PageResult<Song>> getSongPage(
            @Parameter(description = "歌曲名称") @RequestParam(required = false) String name,
            @Parameter(description = "歌手ID") @RequestParam(required = false) Long artistId,
            @Parameter(description = "专辑ID") @RequestParam(required = false) Long albumId,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Song> result = songService.getSongPage(name, artistId, albumId, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("获取歌曲列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/artist/{artistId}")
    @Operation(summary = "获取歌手的歌曲列表", description = "根据歌手ID获取其所有歌曲")
    public ApiResult<List<Song>> getSongsByArtist(
            @Parameter(description = "歌手ID") @PathVariable Long artistId) {
        
        try {
            List<Song> songs = songService.getSongsByArtistId(artistId);
            return ApiResult.success(songs);
        } catch (Exception e) {
            return ApiResult.error("获取歌手歌曲失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/album/{albumId}")
    @Operation(summary = "获取专辑的歌曲列表", description = "根据专辑ID获取其所有歌曲")
    public ApiResult<List<Song>> getSongsByAlbum(
            @Parameter(description = "专辑ID") @PathVariable Long albumId) {
        
        try {
            List<Song> songs = songService.getSongsByAlbumId(albumId);
            return ApiResult.success(songs);
        } catch (Exception e) {
            return ApiResult.error("获取专辑歌曲失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索歌曲", description = "根据关键词搜索歌曲")
    public ApiResult<PageResult<Song>> searchSongs(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Song> result = songService.searchSongs(keyword, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("搜索歌曲失败: " + e.getMessage());
        }
    }
    
    // 保留原有的接口以保证兼容性
    @Operation(summary = "获取歌曲列表", description = "分页获取歌曲列表")
    @GetMapping("/list")
    public ApiResult<PageResult<SongResponse>> getSongList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<SongResponse> result = songService.getSongList(page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "收藏歌曲", description = "收藏或取消收藏歌曲")
    @PostMapping("/favorite")
    public ApiResult<Boolean> favoriteSong(@RequestParam Long userId,
                                           @RequestParam Long songId,
                                           @RequestParam String action) {
        try {
            boolean isFavorited = songService.favoriteSong(userId, songId, action);
            String message = "like".equals(action) ? "收藏成功" : "取消收藏成功";
            return ApiResult.success(message, isFavorited);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户收藏歌曲", description = "分页获取用户收藏的歌曲列表")
    @GetMapping("/users/{userId}/favorites")
    public ApiResult<PageResult<SongResponse>> getUserFavoriteSongs(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<SongResponse> result = songService.getUserFavoriteSongs(userId, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("获取用户收藏歌曲失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "检查歌曲收藏状态", description = "检查用户是否收藏了指定歌曲")
    @GetMapping("/{songId}/favorite-status")
    public ApiResult<Map<String, Object>> checkFavoriteStatus(
            @Parameter(description = "歌曲ID") @PathVariable Long songId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            boolean isFavorited = songService.isSongFavorited(userId, songId);
            Map<String, Object> result = new HashMap<>();
            result.put("isFavorited", isFavorited);
            result.put("songId", songId);
            result.put("userId", userId);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("检查歌曲收藏状态失败: " + e.getMessage());
        }
    }
}