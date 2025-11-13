package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.entity.Banner;
import com.chaoshi.mapper.BannerMapper;
import com.chaoshi.service.BannerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 轮播图Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    
    private final BannerMapper bannerMapper;
    
    @Override
    public List<Banner> getEnabledBanners() {
        try {
            List<Banner> banners = bannerMapper.selectEnabledBanners();
            log.info("获取启用的轮播图成功，数量：{}", banners.size());
            return banners;
        } catch (Exception e) {
            log.error("获取启用的轮播图失败", e);
            throw new RuntimeException("获取启用的轮播图失败: " + e.getMessage());
        }
    }
    
    @Override
    public Banner getBannerById(Long id) {
        try {
            Banner banner = bannerMapper.selectById(id);
            if (banner == null) {
                throw new RuntimeException("轮播图不存在");
            }
            return banner;
        } catch (Exception e) {
            log.error("获取轮播图失败: {}", id, e);
            throw new RuntimeException("获取轮播图失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Banner addBanner(Banner banner) {
        try {
            banner.setDeleted(0);
            banner.setEnabled(true);
            int result = bannerMapper.insertBanner(banner);
            if (result > 0) {
                log.info("添加轮播图成功: {}", banner.getId());
                return banner;
            } else {
                throw new RuntimeException("添加轮播图失败");
            }
        } catch (Exception e) {
            log.error("添加轮播图失败", e);
            throw new RuntimeException("添加轮播图失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Banner updateBanner(Banner banner) {
        try {
            Banner existingBanner = getBannerById(banner.getId());
            if (existingBanner == null) {
                throw new RuntimeException("轮播图不存在");
            }
            
            int result = bannerMapper.updateBanner(banner);
            if (result > 0) {
                log.info("更新轮播图成功: {}", banner.getId());
                return getBannerById(banner.getId());
            } else {
                throw new RuntimeException("更新轮播图失败");
            }
        } catch (Exception e) {
            log.error("更新轮播图失败: {}", banner.getId(), e);
            throw new RuntimeException("更新轮播图失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteBanner(Long id) {
        try {
            int result = bannerMapper.deleteBanner(id);
            log.info("删除轮播图成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除轮播图失败: {}", id, e);
            throw new RuntimeException("删除轮播图失败: " + e.getMessage());
        }
    }
}


