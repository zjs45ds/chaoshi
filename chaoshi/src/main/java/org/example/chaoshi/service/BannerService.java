package org.example.chaoshi.service;

import org.example.chaoshi.entity.Banner;
import java.util.List;

/**
 * 轮播图Service接口
 */
public interface BannerService {
    
    /**
     * 获取所有启用的轮播图
     */
    List<Banner> getEnabledBanners();
    
    /**
     * 根据ID获取轮播图
     */
    Banner getBannerById(Long id);
    
    /**
     * 添加轮播图
     */
    Banner addBanner(Banner banner);
    
    /**
     * 更新轮播图
     */
    Banner updateBanner(Banner banner);
    
    /**
     * 删除轮播图
     */
    boolean deleteBanner(Long id);
}