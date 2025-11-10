package com.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.chaoshi.entity.Banner;
import java.util.List;

/**
 * 轮播图Mapper接口
 */
@Mapper
public interface BannerMapper {
    
    /**
     * 查询所有启用的轮播图
     */
    List<Banner> selectEnabledBanners();
    
    /**
     * 根据ID查询轮播图
     */
    Banner selectById(Long id);
    
    /**
     * 插入轮播图
     */
    int insertBanner(Banner banner);
    
    /**
     * 更新轮播图
     */
    int updateBanner(Banner banner);
    
    /**
     * 删除轮播图
     */
    int deleteBanner(Long id);
}