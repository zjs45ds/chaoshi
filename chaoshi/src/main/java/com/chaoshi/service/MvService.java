package com.chaoshi.service;

import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Mv;
import java.util.List;

public interface MvService {
    
    /**
     * 创建MV
     */
    Mv createMv(Mv mv);
    
    /**
     * 根据ID获取MV
     */
    Mv getMvById(Long id);
    
    /**
     * 更新MV信息
     */
    Mv updateMv(Long id, Mv mv);
    
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
    
    /**
     * 收藏/取消收藏MV
     * @param userId 用户ID
     * @param mvId MV ID
     * @param action 操作类型：like/unlike
     * @return 是否收藏
     */
    boolean favoriteMv(Long userId, Long mvId, String action);
    
    /**
     * 检查用户是否收藏了指定MV
     * @param userId 用户ID
     * @param mvId MV ID
     * @return 是否收藏
     */
    boolean isMvFavorited(Long userId, Long mvId);
    
    /**
     * 获取用户收藏的MV列表
     * @param userId 用户ID
     * @return 用户收藏的MV列表
     */
    List<Mv> getUserFavoriteMvs(Long userId);
}