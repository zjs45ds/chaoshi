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
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

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
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建MV", description = "创建新的MV，支持视频文件和封面图片上传")
    public ApiResult<Mv> createMv(
            @Parameter(description = "MV信息") @Valid @ModelAttribute Mv mv,
            @Parameter(description = "视频文件") @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @Parameter(description = "封面图片") @RequestParam(value = "cover", required = false) MultipartFile coverFile) {
        
        try {
            Mv result = mvService.createMv(mv, videoFile, coverFile);
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
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新MV", description = "更新MV信息，支持更新视频文件和封面图片")
    public ApiResult<Mv> updateMv(
            @Parameter(description = "MV ID") @PathVariable Long id,
            @Parameter(description = "MV信息") @Valid @ModelAttribute Mv mv,
            @Parameter(description = "视频文件") @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @Parameter(description = "封面图片") @RequestParam(value = "cover", required = false) MultipartFile coverFile) {
        
        try {
            Mv result = mvService.updateMv(id, mv, videoFile, coverFile);
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
}