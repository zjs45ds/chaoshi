package org.example.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.ApiResult;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Mv;
import org.example.chaoshi.service.MvService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MV控制器
 */
@RestController
@RequestMapping("/api/mvs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MV管理", description = "MV的增删改查操作")
@Validated
@CrossOrigin(origins = "*")
public class MvController {
    
    private final MvService mvService;
    
    @PostMapping
    @Operation(summary = "创建MV", description = "创建新的MV")
    public ApiResult<Mv> createMv(
            @Parameter(description = "MV信息") @Valid @RequestBody Mv mv) {
        
        try {
            Mv result = mvService.createMv(mv);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("创建MV失败", e);
            return ApiResult.error("创建MV失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取MV详情", description = "根据ID获取MV详细信息")
    public ApiResult<Mv> getMv(
            @Parameter(description = "MV ID") @PathVariable Long id) {
        
        try {
            Mv mv = mvService.getMvById(id);
            return ApiResult.success(mv);
        } catch (Exception e) {
            log.error("获取MV失败: {}", id, e);
            return ApiResult.error("获取MV失败: " + e.getMessage());
        }
    }

    
    @PutMapping("/{id}")
    @Operation(summary = "更新MV", description = "更新MV信息")
    public ApiResult<Mv> updateMv(
            @Parameter(description = "MV ID") @PathVariable Long id,
            @Parameter(description = "MV信息") @Valid @RequestBody Mv mv) {
        
        try {
            Mv result = mvService.updateMv(id, mv);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("更新MV失败: {}", id, e);
            return ApiResult.error("更新MV失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除MV", description = "根据ID删除MV")
    public ApiResult<Boolean> deleteMv(
            @Parameter(description = "MV ID") @PathVariable Long id) {
        
        try {
            boolean result = mvService.deleteMv(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("删除MV失败: {}", id, e);
            return ApiResult.error("删除MV失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除MV", description = "根据ID列表批量删除MV")
    public ApiResult<Boolean> deleteMvs(
            @Parameter(description = "MV ID列表") @RequestBody List<Long> ids) {
        
        try {
            boolean result = mvService.deleteMvs(ids);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("批量删除MV失败", e);
            return ApiResult.error("批量删除MV失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "分页获取MV列表", description = "支持按名称、歌手筛选")
    public ApiResult<PageResult<Mv>> getMvPage(
            @Parameter(description = "MV名称") @RequestParam(required = false) String name,
            @Parameter(description = "歌手ID") @RequestParam(required = false) Long artistId,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Mv> result = mvService.getMvPage(name, artistId, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取MV列表失败", e);
            return ApiResult.error("获取MV列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/artist/{artistId}")
    @Operation(summary = "获取歌手的MV列表", description = "根据歌手ID获取其所有MV")
    public ApiResult<List<Mv>> getMvsByArtist(
            @Parameter(description = "歌手ID") @PathVariable Long artistId) {
        
        try {
            List<Mv> mvs = mvService.getMvsByArtistId(artistId);
            return ApiResult.success(mvs);
        } catch (Exception e) {
            log.error("获取歌手MV失败: {}", artistId, e);
            return ApiResult.error("获取歌手MV失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索MV", description = "根据关键词搜索MV")
    public ApiResult<PageResult<Mv>> searchMvs(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            PageResult<Mv> result = mvService.searchMvs(keyword, page, size);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("搜索MV失败", e);
            return ApiResult.error("搜索MV失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/popular")
    @Operation(summary = "获取热门MV", description = "获取播放量最高的MV列表")
    public ApiResult<List<Mv>> getPopularMvs(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Mv> mvs = mvService.getPopularMvs(limit);
            return ApiResult.success(mvs);
        } catch (Exception e) {
            log.error("获取热门MV失败", e);
            return ApiResult.error("获取热门MV失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/latest")
    @Operation(summary = "获取最新MV", description = "获取最新发布的MV列表")
    public ApiResult<List<Mv>> getLatestMvs(
            @Parameter(description = "数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Mv> mvs = mvService.getLatestMvs(limit);
            return ApiResult.success(mvs);
        } catch (Exception e) {
            log.error("获取最新MV失败", e);
            return ApiResult.error("获取最新MV失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/play")
    @Operation(summary = "增加播放次数", description = "播放MV时调用此接口增加播放次数")
    public ApiResult<Boolean> playMv(
            @Parameter(description = "MV ID") @PathVariable Long id) {
        
        try {
            boolean result = mvService.incrementPlayCount(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("增加MV播放次数失败: {}", id, e);
            return ApiResult.error("增加播放次数失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "收藏/取消收藏MV", description = "收藏或取消收藏指定MV")
    public ApiResult<Map<String, Object>> favoriteMv(
            @Parameter(description = "MV ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "操作类型：like/unlike") @RequestParam String action) {
        
        try {
            boolean isFavorited = mvService.favoriteMv(userId, id, action);
            
            Map<String, Object> result = new HashMap<>();
            result.put("mvId", id);
            result.put("isFavorited", isFavorited);
            result.put("action", action);
            
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("MV收藏操作失败: mvId={}, userId={}, action={}", id, userId, action, e);
            return ApiResult.error("MV收藏操作失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/favorite-status")
    @Operation(summary = "检查MV收藏状态", description = "检查用户是否收藏了指定MV")
    public ApiResult<Map<String, Object>> getMvFavoriteStatus(
            @Parameter(description = "MV ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        try {
            boolean isFavorited = mvService.isMvFavorited(userId, id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("mvId", id);
            result.put("isFavorited", isFavorited);
            
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("检查MV收藏状态失败: mvId={}, userId={}", id, userId, e);
            return ApiResult.error("检查MV收藏状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/favorites")
    @Operation(summary = "获取用户收藏的MV", description = "获取用户收藏的MV列表")
    public ApiResult<List<Mv>> getUserFavoriteMvs(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        try {
            List<Mv> mvs = mvService.getUserFavoriteMvs(userId);
            return ApiResult.success(mvs);
        } catch (Exception e) {
            log.error("获取用户收藏MV失败: userId={}", userId, e);
            return ApiResult.error("获取用户收藏MV失败: " + e.getMessage());
        }
    }
}