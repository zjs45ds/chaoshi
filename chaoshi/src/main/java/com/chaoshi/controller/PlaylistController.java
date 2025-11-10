package com.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.ApiResult;
import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Playlist;
import com.chaoshi.entity.Song;
import com.chaoshi.service.PlaylistService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 歌单控制器
 */
@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "歌单管理", description = "歌单的增删改查操作")
@Validated
@CrossOrigin(origins = "*")
public class PlaylistController {
    
    private final PlaylistService playlistService;

    @GetMapping("/{id}")
    @Operation(summary = "获取歌单详情", description = "根据ID获取歌单详细信息")
    public ApiResult<Playlist> getPlaylist(
            @Parameter(description = "歌单ID") @PathVariable Long id) {
        
        try {
            Playlist playlist = playlistService.getPlaylistById(id);
            return ApiResult.success(playlist);
        } catch (Exception e) {
            return ApiResult.error("获取歌单失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新歌单", description = "更新歌单信息")
    public ApiResult<Playlist> updatePlaylist(
            @Parameter(description = "歌单ID") @PathVariable Long id,
            @Parameter(description = "歌单信息") @Valid @RequestBody Playlist playlist) {
        
        try {
            Playlist result = playlistService.updatePlaylist(id, playlist);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("更新歌单失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除歌单", description = "根据ID删除歌单")
    public ApiResult<Boolean> deletePlaylist(
            @Parameter(description = "歌单ID") @PathVariable Long id) {
        
        try {
            boolean result = playlistService.deletePlaylist(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("删除歌单失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "分页获取歌单列表", description = "支持按名称、用户、公开状态筛选")
    public ApiResult<PageResult<Playlist>> getPlaylistPage(
            @Parameter(description = "歌单名称") @RequestParam(required = false) String name,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "是否公开") @RequestParam(required = false) Boolean isPublic,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Playlist> result = playlistService.getPlaylistPage(name, userId, isPublic, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("获取歌单列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的歌单列表", description = "根据用户ID获取其所有歌单")
    public ApiResult<List<Playlist>> getPlaylistsByUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            List<Playlist> playlists = playlistService.getPlaylistsByUserId(userId);
            return ApiResult.success(playlists);
        } catch (Exception e) {
            return ApiResult.error("获取用户歌单失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/public")
    @Operation(summary = "获取公开歌单", description = "获取公开的歌单列表")
    public ApiResult<PageResult<Playlist>> getPublicPlaylists(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Playlist> result = playlistService.getPublicPlaylists(page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("获取公开歌单失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索歌单", description = "根据关键词搜索歌单")
    public ApiResult<PageResult<Playlist>> searchPlaylists(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Playlist> result = playlistService.searchPlaylists(keyword, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("搜索歌单失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/songs")
    @Operation(summary = "获取歌单中的歌曲", description = "根据歌单ID获取其包含的所有歌曲")
    public ApiResult<List<Song>> getPlaylistSongs(
            @Parameter(description = "歌单ID") @PathVariable Long id) {
        
        try {
            List<Song> songs = playlistService.getPlaylistSongs(id);
            return ApiResult.success(songs);
        } catch (Exception e) {
            return ApiResult.error("获取歌单歌曲失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/songs/{songId}")
    @Operation(summary = "添加歌曲到歌单", description = "将指定歌曲添加到歌单中")
    public ApiResult<Boolean> addSongToPlaylist(
            @Parameter(description = "歌单ID") @PathVariable Long id,
            @Parameter(description = "歌曲ID") @PathVariable Long songId) {
        
        try {
            boolean result = playlistService.addSongToPlaylist(id, songId);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("添加歌曲到歌单失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}/songs/{songId}")
    @Operation(summary = "从歌单移除歌曲", description = "将指定歌曲从歌单中移除")
    public ApiResult<Boolean> removeSongFromPlaylist(
            @Parameter(description = "歌单ID") @PathVariable Long id,
            @Parameter(description = "歌曲ID") @PathVariable Long songId) {
        
        try {
            boolean result = playlistService.removeSongFromPlaylist(id, songId);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("从歌单移除歌曲失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/songs/order")
    @Operation(summary = "更新歌单中歌曲的排序", description = "重新排序歌单中的歌曲")
    public ApiResult<Boolean> updateSongOrder(
            @Parameter(description = "歌单ID") @PathVariable Long id,
            @Parameter(description = "歌曲ID列表") @RequestBody List<Long> songIds) {
        
        try {
            boolean result = playlistService.updateSongOrder(id, songIds);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("更新歌单歌曲排序失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}/songs")
    @Operation(summary = "清空歌单", description = "移除歌单中的所有歌曲")
    public ApiResult<Boolean> clearPlaylist(
            @Parameter(description = "歌单ID") @PathVariable Long id) {
        
        try {
            boolean result = playlistService.clearPlaylist(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("清空歌单失败: " + e.getMessage());
        }
    }
}