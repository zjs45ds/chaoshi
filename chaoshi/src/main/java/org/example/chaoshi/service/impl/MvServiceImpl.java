package org.example.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Mv;
import org.example.chaoshi.mapper.MvMapper;
import org.example.chaoshi.service.FileUploadService;
import org.example.chaoshi.service.MvService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MV服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MvServiceImpl implements MvService {
    
    private final MvMapper mvMapper;
    private final FileUploadService fileUploadService;
    
    @Override
    @Transactional
    public Mv createMv(Mv mv, MultipartFile videoFile, MultipartFile coverFile) {
        try {
            // 上传视频文件
            if (videoFile != null && !videoFile.isEmpty()) {
                String videoPath = fileUploadService.uploadFile(videoFile, "video");
                mv.setVideoPath(videoPath);
            }
            
            // 上传封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                String cover = fileUploadService.uploadFile(coverFile, "image");
                mv.setCover(cover);
            }
            
            mv.setPlayCount(0L);
            mv.setCreatedAt(LocalDateTime.now());
            mv.setUpdatedAt(LocalDateTime.now());
            mvMapper.insertMv(mv);
            
            log.info("MV创建成功: {}", mv.getId());
            return mv;
        } catch (Exception e) {
            log.error("创建MV失败", e);
            throw new RuntimeException("创建MV失败: " + e.getMessage());
        }
    }
    
    @Override
    public Mv getMvById(Long id) {
        Mv mv = mvMapper.selectById(id);
        if (mv == null) {
            throw new RuntimeException("MV不存在: " + id);
        }
        return mv;
    }
    
    @Override
    @Transactional
    public Mv updateMv(Long id, Mv mv, MultipartFile videoFile, MultipartFile coverFile) {
        try {
            Mv existingMv = getMvById(id);
            
            // 上传新视频文件
            if (videoFile != null && !videoFile.isEmpty()) {
                // 删除旧视频
                if (existingMv.getVideoPath() != null) {
                    fileUploadService.deleteFile(existingMv.getVideoPath());
                }
                String videoPath = fileUploadService.uploadFile(videoFile, "video");
                mv.setVideoPath(videoPath);
            }
            
            // 上传新封面图片
            if (coverFile != null && !coverFile.isEmpty()) {
                // 删除旧封面
                if (existingMv.getCover() != null) {
                    fileUploadService.deleteFile(existingMv.getCover());
                }
                String cover = fileUploadService.uploadFile(coverFile, "image");
                mv.setCover(cover);
            }
            
            mv.setId(id);
            mv.setUpdatedAt(LocalDateTime.now());
            mvMapper.updateMv(mv);
            
            log.info("MV更新成功: {}", id);
            return getMvById(id);
        } catch (Exception e) {
            log.error("更新MV失败: {}", id, e);
            throw new RuntimeException("更新MV失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteMv(Long id) {
        try {
            Mv mv = getMvById(id);
            
            // 删除相关文件
            if (mv.getVideoPath() != null) {
                fileUploadService.deleteFile(mv.getVideoPath());
            }
            if (mv.getCover() != null) {
                fileUploadService.deleteFile(mv.getCover());
            }
            
            int result = mvMapper.deleteMv(id);
            
            log.info("MV删除成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除MV失败: {}", id, e);
            throw new RuntimeException("删除MV失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteMvs(List<Long> ids) {
        try {
            // 删除相关文件
            for (Long id : ids) {
                Mv mv = mvMapper.selectById(id);
                if (mv != null) {
                    if (mv.getVideoPath() != null) {
                        fileUploadService.deleteFile(mv.getVideoPath());
                    }
                    if (mv.getCover() != null) {
                        fileUploadService.deleteFile(mv.getCover());
                    }
                }
            }
            
            int result = mvMapper.deleteBatch(ids);
            
            log.info("批量删除MV成功，数量: {}", ids.size());
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除MV失败", e);
            throw new RuntimeException("批量删除MV失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Mv> getMvPage(String name, Long artistId, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Mv> mvs = mvMapper.selectPage(name, artistId, offset, (long) size);
            Long total = mvMapper.countMvs(name, artistId);
            
            return new PageResult<>(total, mvs, page, size);
        } catch (Exception e) {
            log.error("分页查询MV失败", e);
            throw new RuntimeException("分页查询MV失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Mv> getMvsByArtistId(Long artistId) {
        try {
            return mvMapper.selectByArtistId(artistId);
        } catch (Exception e) {
            log.error("根据歌手ID查询MV失败: {}", artistId, e);
            throw new RuntimeException("根据歌手ID查询MV失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<Mv> searchMvs(String keyword, Integer page, Integer size) {
        try {
            long offset = (long) (page - 1) * size;
            
            List<Mv> mvs = mvMapper.searchMvs(keyword, offset, (long) size);
            // 注意：搜索的总数需要单独查询
            Long total = mvMapper.countMvs(keyword, null);
            
            return new PageResult<>(total, mvs, page, size);
        } catch (Exception e) {
            log.error("搜索MV失败", e);
            throw new RuntimeException("搜索MV失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Mv> getPopularMvs(Integer limit) {
        try {
            return mvMapper.selectPopularMvs(limit);
        } catch (Exception e) {
            log.error("获取热门MV失败", e);
            throw new RuntimeException("获取热门MV失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Mv> getLatestMvs(Integer limit) {
        try {
            return mvMapper.selectLatestMvs(limit);
        } catch (Exception e) {
            log.error("获取最新MV失败", e);
            throw new RuntimeException("获取最新MV失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean incrementPlayCount(Long id) {
        try {
            int result = mvMapper.incrementPlayCount(id);
            log.debug("MV播放次数增加成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("增加MV播放次数失败: {}", id, e);
            return false;
        }
    }
    
}