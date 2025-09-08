package org.example.chaoshi.service;

import org.example.chaoshi.dto.PageResult;
import org.example.chaoshi.entity.Mv;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * MV Service接口
 */
public interface MvService {
    
    /**
     * 创建MV
     */
    Mv createMv(Mv mv, MultipartFile videoFile, MultipartFile coverFile);
    
    /**
     * 根据ID获取MV
     */
    Mv getMvById(Long id);
    
    /**
     * 更新MV信息
     */
    Mv updateMv(Long id, Mv mv, MultipartFile videoFile, MultipartFile coverFile);
    
    /**
     * 删除MV
     */
    boolean deleteMv(Long id);
    
    /**
     * 批量删除MV
     */
    boolean deleteMvs(List<Long> ids);
    
    /**
     * 分页查询MV
     */
    PageResult<Mv> getMvPage(String name, Long artistId, Integer page, Integer size);
    
    /**
     * 根据歌手ID获取MV列表
     */
    List<Mv> getMvsByArtistId(Long artistId);
    
    /**
     * 搜索MV
     */
    PageResult<Mv> searchMvs(String keyword, Integer page, Integer size);
    
    /**
     * 获取热门MV
     */
    List<Mv> getPopularMvs(Integer limit);
    
    /**
     * 获取最新MV
     */
    List<Mv> getLatestMvs(Integer limit);
    
    /**
     * 增加播放次数
     */
    boolean incrementPlayCount(Long id);
}