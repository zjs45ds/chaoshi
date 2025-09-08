package org.example.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.ApiResult;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Toplist;
import org.example.chaoshi.service.ToplistService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

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
}