package com.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.chaoshi.dto.ApiResult;
import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Artist;
import com.chaoshi.entity.Album;
import com.chaoshi.entity.Banner;
import com.chaoshi.entity.Playlist;
import com.chaoshi.entity.Toplist;
import com.chaoshi.service.AlbumService;
import com.chaoshi.service.ArtistService;
import com.chaoshi.service.BannerService;
import com.chaoshi.service.PlaylistService;
import com.chaoshi.service.SongService;
import com.chaoshi.service.ToplistService;
import com.chaoshi.service.MvService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 首页Controller
 */
@Tag(name = "首页", description = "首页相关接口")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final PlaylistService playlistService;
    private final ToplistService toplistService;
    private final MvService mvService;
    private final BannerService bannerService;
    
    @Operation(summary = "首页", description = "应用首页接口")
    @GetMapping("")
    public ApiResult<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>();
        data.put("application", "潮石音乐");
        data.put("version", "1.0.0");
        data.put("description", "潮石音乐后端API服务");
        data.put("timestamp", LocalDateTime.now());
        data.put("status", "running");
        
        return ApiResult.success("欢迎使用潮石音乐API", data);
    }
    
    @Operation(summary = "健康检查", description = "应用健康检查接口")
    @GetMapping("/health")
    public ApiResult<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("version", "1.0.0");
        
        return ApiResult.success("服务运行正常", data);
    }
    
    @Operation(summary = "应用信息", description = "获取应用基本信息")
    @GetMapping("/info")
    public ApiResult<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "潮石音乐");
        data.put("version", "1.0.0");
        data.put("description", "基于Spring Boot的音乐流媒体平台后端服务");
        data.put("author", "潮石音乐开发团队");
        data.put("contact", "dev@chaoshi-music.com");
        data.put("build_time", LocalDateTime.now());
        
        return ApiResult.success(data);
    }
    
    // ====== 首页数据 API 接口 ======
    
    @GetMapping("/api/home/banners")
    public ApiResult<List<Map<String, Object>>> getBanners() {
        try {

            List<Banner> bannersList = bannerService.getEnabledBanners();
            
            if (bannersList != null && !bannersList.isEmpty()) {
                List<Map<String, Object>> banners = bannersList.stream()
                    .map(banner -> {
                        Map<String, Object> bannerMap = new HashMap<>();
                        bannerMap.put("id", banner.getId());
                          bannerMap.put("title", banner.getTitle());
                          bannerMap.put("imgUrl", banner.getImgUrl());
                          return bannerMap;
                    })
                    .toList();
                
                return ApiResult.success("获取轮播图成功", banners);
            }

            return ApiResult.success("获取轮播图成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取轮播图失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取推荐歌单", description = "获取首页推荐歌单列表")
    @GetMapping("/api/home/recommend/playlists")
    public ApiResult<List<Map<String, Object>>> getRecommendPlaylists(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            PageResult<Playlist> playlistsData = playlistService.getPublicPlaylists(1, limit);
            
            if (playlistsData != null && playlistsData.getContent() != null && !playlistsData.getContent().isEmpty()) {
                List<Map<String, Object>> playlists = playlistsData.getContent().stream()
                    .map(playlist -> {
                        Map<String, Object> playlistMap = new HashMap<>();
                        playlistMap.put("id", playlist.getId());
                        playlistMap.put("name", playlist.getName());
                        playlistMap.put("cover", playlist.getCoverUrl());
                        playlistMap.put("playCount", "未知"); 
                        return playlistMap;
                    })
                    .toList();
                    
                return ApiResult.success("获取推荐歌单成功", playlists);
            }

            return ApiResult.success("获取推荐歌单成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取推荐歌单失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取热门歌手", description = "获取首页热门歌手列表")
    @GetMapping("/api/home/hot/artists")
    public ApiResult<List<Map<String, Object>>> getHotArtists(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Artist> artistsList = artistService.getPopularArtists(limit);
            
            if (artistsList != null && !artistsList.isEmpty()) {
                List<Map<String, Object>> artists = artistsList.stream()
                    .map(artist -> {
                        Map<String, Object> artistMap = new HashMap<>();
                        artistMap.put("id", artist.getId());
                        artistMap.put("name", artist.getName());
                        artistMap.put("avatar", artist.getAvatar());
                        artistMap.put("description", artist.getDescription());
                        return artistMap;
                    })
                    .toList();
                return ApiResult.success("获取热门歌手成功", artists);
            }

            return ApiResult.success("获取热门歌手成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取热门歌手失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取热门专辑", description = "获取首页热门专辑列表")
    @GetMapping("/api/home/hot/albums")
    public ApiResult<List<Map<String, Object>>> getHotAlbums(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Album> albumsList = albumService.getLatestAlbums(limit);
            
            if (albumsList != null && !albumsList.isEmpty()) {
                List<Map<String, Object>> albums = albumsList.stream()
                    .map(album -> {
                        Map<String, Object> albumMap = new HashMap<>();
                        albumMap.put("id", album.getId());
                        albumMap.put("name", album.getName());
                        albumMap.put("cover", album.getCover());
                        try {
                            Artist artist = artistService.getArtistById(album.getArtistId());
                            albumMap.put("artist", artist != null ? artist.getName() : "未知歌手");
                        } catch (Exception e) {
                            albumMap.put("artist", "未知歌手");
                        }
                        albumMap.put("releaseDate", album.getReleaseDate());
                        return albumMap;
                    })
                    .toList();
                return ApiResult.success("获取热门专辑成功", albums);
            }

            return ApiResult.success("获取热门专辑成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取热门专辑失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取热门歌曲", description = "获取首页热门歌曲列表")
    @GetMapping("/api/home/hot/songs")
    public ApiResult<List<Map<String, Object>>> getHotSongs(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            var songsData = songService.getSongList(1, limit);
            
            if (songsData != null && songsData.getContent() != null && !songsData.getContent().isEmpty()) {
                List<Map<String, Object>> songs = songsData.getContent().stream()
                    .map(song -> {
                        Map<String, Object> songMap = new HashMap<>();
                        songMap.put("id", song.getId());
                        songMap.put("name", song.getName());
                        songMap.put("cover", song.getCover());
                        songMap.put("artist", song.getArtistName());
                        songMap.put("album", song.getAlbumName());
                        songMap.put("duration", song.getDuration());
                        return songMap;
                    })
                    .toList();
                return ApiResult.success("获取热门歌曲成功", songs);
            }
            
            return ApiResult.success("获取热门歌曲成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取热门歌曲失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取热门排行榜", description = "获取首页热门排行榜列表")
    @GetMapping("/api/home/hot/toplists")
    public ApiResult<List<Map<String, Object>>> getHotToplists(
            @RequestParam(defaultValue = "10") int limit) {
        try {

            List<Toplist> toplistsList = toplistService.getHotToplists(limit);
            
            if (toplistsList != null && !toplistsList.isEmpty()) {
                List<Map<String, Object>> toplists = toplistsList.stream()
                    .map(toplist -> {
                        Map<String, Object> toplistMap = new HashMap<>();
                        toplistMap.put("id", toplist.getId());
                        toplistMap.put("name", toplist.getName());
                        toplistMap.put("cover", toplist.getCover());
                        toplistMap.put("description", toplist.getDescription());
                        return toplistMap;
                    })
                    .toList();
                return ApiResult.success("获取热门排行榜成功", toplists);
            }

            return ApiResult.success("获取热门排行榜成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取热门排行榜失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取热门MV", description = "获取首页热门MV列表")
    @GetMapping("/api/home/hot/mvs")
    public ApiResult<List<Map<String, Object>>> getHotMvs(
            @RequestParam(defaultValue = "10") int limit) {
        try {

            var mvsData = mvService.getPopularMvs(limit);
            
            if (mvsData != null && !mvsData.isEmpty()) {

                List<Map<String, Object>> mvs = mvsData.stream()
                    .map(mv -> {
                        Map<String, Object> mvMap = new HashMap<>();
                        mvMap.put("id", mv.getId());
                        mvMap.put("name", mv.getName());
                        mvMap.put("cover", mv.getCover());
                        mvMap.put("videoUrl", mv.getVideoPath());
                        mvMap.put("artistId", mv.getArtistId());
                        mvMap.put("playCount", mv.getPlayCount());
                        mvMap.put("duration", mv.getDuration());
                        return mvMap;
                    })
                    .toList();
                return ApiResult.success("获取热门MV成功", mvs);
            }
            
            return ApiResult.success("获取热门MV成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取热门MV失败: " + e.getMessage());
        }
    }
    
}