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
import org.example.chaoshi.service.AlbumService;
import org.example.chaoshi.service.ArtistService;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 专辑控制器
 */
@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "专辑管理", description = "专辑的增删改查操作")
@Validated
@CrossOrigin(origins = "*")
public class AlbumController {
    
    private final AlbumService albumService;
    private final ArtistService artistService;

    
    @PostMapping
    @Operation(summary = "创建专辑", description = "创建新的专辑")
    public ApiResult<Album> createAlbum(
            @Parameter(description = "专辑信息") @Valid @RequestBody Album album) {
        
        try {
            Album result = albumService.createAlbum(album);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("创建专辑失败", e);
            return ApiResult.error("创建专辑失败: " + e.getMessage());
        }
    }
    

    
    @GetMapping("/{id}")
    @Operation(summary = "获取专辑详情", description = "根据ID获取专辑详细信息")
    public ApiResult<Album> getAlbum(
            @Parameter(description = "专辑ID") @PathVariable Long id) {
        
        try {
            Album album = albumService.getAlbumById(id);
            // 添加默认封面支持
            if (album != null && (album.getCover() == null || album.getCover().isEmpty())) {
                album.setCover("/src/assets/1音乐.png");
            }
            return ApiResult.success(album);
        } catch (Exception e) {
            log.error("获取专辑失败: {}", id, e);
            return ApiResult.error("获取专辑失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新专辑", description = "更新专辑信息")
    public ApiResult<Album> updateAlbum(
            @Parameter(description = "专辑ID") @PathVariable Long id,
            @Parameter(description = "专辑信息") @Valid @RequestBody Album album) {
        
        try {
            Album result = albumService.updateAlbum(id, album);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("更新专辑失败: {}", id, e);
            return ApiResult.error("更新专辑失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除专辑", description = "根据ID删除专辑")
    public ApiResult<Boolean> deleteAlbum(
            @Parameter(description = "专辑ID") @PathVariable Long id) {
        
        try {
            boolean result = albumService.deleteAlbum(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("删除专辑失败: {}", id, e);
            return ApiResult.error("删除专辑失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除专辑", description = "根据ID列表批量删除专辑")
    public ApiResult<Boolean> deleteAlbums(
            @Parameter(description = "专辑ID列表") @RequestBody List<Long> ids) {
        
        try {
            boolean result = albumService.deleteAlbums(ids);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("批量删除专辑失败", e);
            return ApiResult.error("批量删除专辑失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "分页获取专辑列表", description = "支持按名称、歌手、发行年份筛选")
    public ApiResult<PageResult<Map<String, Object>>> getAlbumPage(
            @Parameter(description = "专辑名称") @RequestParam(required = false) String name,
            @Parameter(description = "歌手ID") @RequestParam(required = false) Long artistId,
            @Parameter(description = "发行年份") @RequestParam(required = false) Integer releaseYear,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Album> result = albumService.getAlbumPage(name, artistId, releaseYear, page, size);
            
            // 转换为包含歌手名称的格式
            List<Map<String, Object>> enhancedAlbums = result.getContent().stream()
                .map(album -> {
                    Map<String, Object> albumMap = new HashMap<>();
                    albumMap.put("id", album.getId());
                    albumMap.put("name", album.getName());
                    // 添加默认封面支持，当cover为空时使用默认图片
                    albumMap.put("cover", album.getCover() != null && !album.getCover().isEmpty() ? album.getCover() : "/src/assets/1音乐.png");
                    albumMap.put("releaseDate", album.getReleaseDate());
                    albumMap.put("description", album.getDescription());
                    albumMap.put("artistId", album.getArtistId());
                    
                    // 获取歌手名称
                    try {
                        Artist artist = artistService.getArtistById(album.getArtistId());
                        albumMap.put("artist", artist != null ? artist.getName() : "未知歌手");
                    } catch (Exception e) {
                        albumMap.put("artist", "未知歌手");
                    }
                    
                    return albumMap;
                })
                .collect(Collectors.toList());
            
            PageResult<Map<String, Object>> enhancedResult = new PageResult<>(
                result.getTotal(), 
                enhancedAlbums, 
                result.getPage(), 
                result.getSize()
            );
            
            return ApiResult.success(enhancedResult);
        } catch (Exception e) {
            log.error("获取专辑列表失败", e);
            return ApiResult.error("获取专辑列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/artist/{artistId}")
    @Operation(summary = "获取歌手的专辑列表", description = "根据歌手ID获取其所有专辑")
    public ApiResult<List<Album>> getAlbumsByArtist(
            @Parameter(description = "歌手ID") @PathVariable Long artistId) {
        
        try {
            List<Album> albums = albumService.getAlbumsByArtistId(artistId);
            // 添加默认封面支持
            for (Album album : albums) {
                if (album.getCover() == null || album.getCover().isEmpty()) {
                    album.setCover("/src/assets/1音乐.png");
                }
            }
            return ApiResult.success(albums);
        } catch (Exception e) {
            log.error("获取歌手专辑失败: {}", artistId, e);
            return ApiResult.error("获取歌手专辑失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索专辑", description = "根据关键词搜索专辑")
    public ApiResult<PageResult<Album>> searchAlbums(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Album> result = albumService.searchAlbums(keyword, page, size);
            // 为结果集中的每个专辑添加默认封面支持
            if (result.getContent() != null) {
                for (Album album : result.getContent()) {
                    if (album.getCover() == null || album.getCover().isEmpty()) {
                        album.setCover("/src/assets/1音乐.png");
                    }
                }
            }
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("搜索专辑失败", e);
            return ApiResult.error("搜索专辑失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/latest")
    @Operation(summary = "获取最新专辑", description = "获取最新发布的专辑列表")
    public ApiResult<List<Album>> getLatestAlbums(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Album> albums = albumService.getLatestAlbums(limit);
            // 添加默认封面支持
            for (Album album : albums) {
                if (album.getCover() == null || album.getCover().isEmpty()) {
                    album.setCover("/src/assets/1音乐.png");
                }
            }
            return ApiResult.success(albums);
        } catch (Exception e) {
            log.error("获取最新专辑失败", e);
            return ApiResult.error("获取最新专辑失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "收藏/取消收藏专辑", description = "收藏或取消收藏指定专辑")
    public ApiResult<Map<String, Object>> favoriteAlbum(
            @Parameter(description = "专辑ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "操作类型：like/unlike") @RequestParam String action) {
        
        try {
            boolean isFavorited = albumService.favoriteAlbum(userId, id, action);
            
            Map<String, Object> result = new HashMap<>();
            result.put("albumId", id);
            result.put("isFavorited", isFavorited);
            result.put("action", action);
            
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("专辑收藏操作失败: albumId={}, userId={}, action={}", id, userId, action, e);
            return ApiResult.error("专辑收藏操作失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/favorite-status")
    @Operation(summary = "检查专辑收藏状态", description = "检查用户是否收藏了指定专辑")
    public ApiResult<Map<String, Object>> getAlbumFavoriteStatus(
            @Parameter(description = "专辑ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        try {
            boolean isFavorited = albumService.isAlbumFavorited(userId, id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("albumId", id);
            result.put("isFavorited", isFavorited);
            
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("检查专辑收藏状态失败: albumId={}, userId={}", id, userId, e);
            return ApiResult.error("检查专辑收藏状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/favorites")
    @Operation(summary = "获取用户收藏的专辑", description = "获取用户收藏的专辑列表")
    public ApiResult<List<Album>> getUserFavoriteAlbums(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        try {
            List<Album> albums = albumService.getUserFavoriteAlbums(userId);
            // 添加默认封面支持
            for (Album album : albums) {
                if (album.getCover() == null || album.getCover().isEmpty()) {
                    album.setCover("/src/assets/1音乐.png");
                }
            }
            return ApiResult.success(albums);
        } catch (Exception e) {
            log.error("获取用户收藏专辑失败: userId={}", userId, e);
            return ApiResult.error("获取用户收藏专辑失败: " + e.getMessage());
        }
    }
}