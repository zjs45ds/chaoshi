package org.example.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.chaoshi.dto.ApiResult;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Artist;
import org.example.chaoshi.entity.Album;
import org.example.chaoshi.entity.Playlist;
import org.example.chaoshi.entity.Toplist;
import org.example.chaoshi.service.AlbumService;
import org.example.chaoshi.service.ArtistService;
import org.example.chaoshi.service.PlaylistService;
import org.example.chaoshi.service.SongService;
import org.example.chaoshi.service.ToplistService;
import org.example.chaoshi.service.MvService;
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
    
    @Operation(summary = "获取首页轮播图", description = "获取首页轮播图列表")
    @GetMapping("/api/home/banners")
    public ApiResult<List<Map<String, Object>>> getBanners() {
        try {
            // 返回模拟轮播图数据
            List<Map<String, Object>> banners = new ArrayList<>();
            
            // 添加轮播图数据
            Map<String, Object> banner1 = new HashMap<>();
            banner1.put("id", 1);
            banner1.put("title", "欢迎来到潮石音乐");
            banner1.put("img", "https://img2.woyaogexing.com/2024/12/19/banner1_welcome.jpg");
            banner1.put("link", "/");
            banners.add(banner1);
            
            Map<String, Object> banner2 = new HashMap<>();
            banner2.put("id", 2);
            banner2.put("title", "周杰伦新专辑首发");
            banner2.put("img", "https://img2.woyaogexing.com/2024/12/19/banner2_jay_new_album.jpg");
            banner2.put("link", "/artist/1");
            banners.add(banner2);
            
            Map<String, Object> banner3 = new HashMap<>();
            banner3.put("id", 3);
            banner3.put("title", "华语金曲榜");
            banner3.put("img", "https://img2.woyaogexing.com/2024/12/19/banner3_chinese_hits.jpg");
            banner3.put("link", "/playlist/1");
            banners.add(banner3);
            
            Map<String, Object> banner4 = new HashMap<>();
            banner4.put("id", 4);
            banner4.put("title", "新歌首发专区");
            banner4.put("img", "https://img2.woyaogexing.com/2024/12/19/banner4_new_releases.jpg");
            banner4.put("link", "/discover");
            banners.add(banner4);
            
            Map<String, Object> banner5 = new HashMap<>();
            banner5.put("id", 5);
            banner5.put("title", "音乐人招募");
            banner5.put("img", "https://img2.woyaogexing.com/2024/12/19/banner5_musician_recruitment.jpg");
            banner5.put("link", "/musician");
            banners.add(banner5);
            
            return ApiResult.success("获取轮播图成功", banners);
        } catch (Exception e) {
            return ApiResult.error("获取轮播图失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取推荐歌单", description = "获取首页推荐歌单列表")
    @GetMapping("/api/home/recommend/playlists")
    public ApiResult<List<Map<String, Object>>> getRecommendPlaylists(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // 从数据库获取公开歌单作为推荐歌单
            PageResult<Playlist> playlistsData = playlistService.getPublicPlaylists(1, limit);
            
            if (playlistsData != null && playlistsData.getContent() != null && !playlistsData.getContent().isEmpty()) {
                List<Map<String, Object>> playlists = playlistsData.getContent().stream()
                    .map(playlist -> {
                        Map<String, Object> playlistMap = new HashMap<>();
                        playlistMap.put("id", playlist.getId());
                        playlistMap.put("name", playlist.getName());
                        playlistMap.put("cover", playlist.getCoverUrl());
                        playlistMap.put("playCount", "未知"); // 播放次数暂时设为未知
                        return playlistMap;
                    })
                    .toList();
                    
                return ApiResult.success("获取推荐歌单成功", playlists);
            }
            
            // 如果数据库没有数据，返回空列表
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
            // 从数据库获取热门歌手数据
            List<Artist> artistsList = artistService.getPopularArtists(limit);
            
            if (artistsList != null && !artistsList.isEmpty()) {
                // 转换为首页需要的格式
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
            
            // 如果数据库没有数据，返回空列表
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
            // 从数据库获取最新专辑作为热门专辑
            List<Album> albumsList = albumService.getLatestAlbums(limit);
            
            if (albumsList != null && !albumsList.isEmpty()) {
                // 转换为首页需要的格式
                List<Map<String, Object>> albums = albumsList.stream()
                    .map(album -> {
                        Map<String, Object> albumMap = new HashMap<>();
                        albumMap.put("id", album.getId());
                        albumMap.put("name", album.getName());
                        albumMap.put("cover", album.getCover());
                        // 获取歌手名称
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
            
            // 如果数据库没有数据，返回空列表
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
            // 从数据库获取歌曲数据
            var songsData = songService.getSongList(1, limit);
            
            if (songsData != null && songsData.getContent() != null && !songsData.getContent().isEmpty()) {
                // 转换为首页需要的格式
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
            
            // 如果数据库没有数据，返回空列表
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
            // 从数据库获取排行榜数据
            List<Toplist> toplistsList = toplistService.getHotToplists(limit);
            
            if (toplistsList != null && !toplistsList.isEmpty()) {
                // 转换为首页需要的格式
                List<Map<String, Object>> toplists = toplistsList.stream()
                    .map(toplist -> {
                        Map<String, Object> toplistMap = new HashMap<>();
                        toplistMap.put("id", toplist.getId());
                        toplistMap.put("name", toplist.getName());
                        toplistMap.put("cover", toplist.getCover());
                        toplistMap.put("description", toplist.getDescription());
                        toplistMap.put("updateFrequency", toplist.getUpdateFrequency());
                        return toplistMap;
                    })
                    .toList();
                return ApiResult.success("获取热门排行榜成功", toplists);
            }
            
            // 如果数据库没有数据，返回空列表
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
            // 从数据库获取MV数据
            var mvsData = mvService.getPopularMvs(limit);
            
            if (mvsData != null && !mvsData.isEmpty()) {
                // 转换为首页需要的格式
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
            
            // 如果数据库没有数据，返回空列表
            return ApiResult.success("获取热门MV成功", new ArrayList<>());
        } catch (Exception e) {
            return ApiResult.error("获取热门MV失败: " + e.getMessage());
        }
    }
    
}