package com.chaoshi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.PageResult;
import com.chaoshi.entity.Mv;
import com.chaoshi.entity.UserFavoriteMv;
import com.chaoshi.mapper.MvMapper;
import com.chaoshi.mapper.UserFavoriteMvMapper;
import com.chaoshi.service.MvService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MV服务实现类（无文件上传功能）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MvServiceImpl implements MvService {
    
    private final MvMapper mvMapper;
    private final UserFavoriteMvMapper userFavoriteMvMapper;

    @Override
    @Transactional
    public Mv createMv(Mv mv) {
        try {
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
        try {
            Mv mv = mvMapper.selectById(id);
            if (mv == null) {
                throw new RuntimeException("MV不存在");
            }
            return mv;
        } catch (Exception e) {
            log.error("获取MV失败: {}", id, e);
            throw new RuntimeException("获取MV失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Mv updateMv(Long id, Mv mv) {
        try {
            Mv existingMv = getMvById(id);
            if (existingMv == null) {
                throw new RuntimeException("MV不存在");
            }
            
            mv.setId(id);
            mv.setUpdatedAt(LocalDateTime.now());
            
            int result = mvMapper.updateMv(mv);
            if (result > 0) {
                log.info("MV更新成功: {}", id);
                return getMvById(id);
            } else {
                throw new RuntimeException("MV更新失败");
            }
        } catch (Exception e) {
            log.error("更新MV失败: {}", id, e);
            throw new RuntimeException("更新MV失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteMv(Long id) {
        try {
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
            int result = mvMapper.deleteBatch(ids);
            log.info("批量删除MV成功，数量: {}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除MV失败", e);
            throw new RuntimeException("批量删除MV失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Mv> getMvPage(String name, Long artistId, Integer page, Integer size) {
        try {
            int offset = (page - 1) * size;
            List<Mv> mvs = mvMapper.selectPage(name, artistId, (long)offset, (long)size);
            Long total = mvMapper.countMvs(name, artistId);
            
            return new PageResult<>(total, mvs, page, size);
        } catch (Exception e) {
            log.error("分页获取MV失败", e);
            throw new RuntimeException("分页获取MV失败: " + e.getMessage());
        }
    }

    @Override
    public List<Mv> getMvsByArtistId(Long artistId) {
        try {
            return mvMapper.selectByArtistId(artistId);
        } catch (Exception e) {
            log.error("根据艺术家获取MV失败: {}", artistId, e);
            throw new RuntimeException("根据艺术家获取MV失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<Mv> searchMvs(String keyword, Integer page, Integer size) {
        try {
            int offset = (page - 1) * size;
            List<Mv> mvs = mvMapper.searchMvs(keyword, (long)offset, (long)size);
            // 为了获取总数，我们需要再做一次不分页的查询来计算总数
            List<Mv> allMvs = mvMapper.searchMvs(keyword, 0L, 10000L);
            
            return new PageResult<>((long)allMvs.size(), mvs, page, size);
        } catch (Exception e) {
            log.error("搜索MV失败: {}", keyword, e);
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
            log.info("MV播放次数增加成功: {}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("增加MV播放次数失败: {}", id, e);
            throw new RuntimeException("增加MV播放次数失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean favoriteMv(Long userId, Long mvId, String action) {
        try {
            // 检查当前收藏状态
            Boolean exists = userFavoriteMvMapper.existsByUserIdAndMvId(userId, mvId);
            boolean currentlyFavorited = exists != null && exists;
            
            if ("like".equals(action)) {
                if (currentlyFavorited) {
                    // 如果已经收藏，直接返回true，不抛出异常
                    log.info("MV已经在收藏夹中，无需重复收藏: userId={}, mvId={}", userId, mvId);
                    return true;
                }
                
                // 尝试恢复已删除的记录，如果失败则插入新记录
                try {

                    int restored = userFavoriteMvMapper.restoreByUserIdAndMvId(userId, mvId, LocalDateTime.now());
                    if (restored > 0) {
                        log.info("恢复MV收藏成功: userId={}, mvId={}", userId, mvId);
                        return true;
                    }
                } catch (Exception restoreEx) {
                    log.debug("恢复收藏记录失败，尝试插入新记录: {}", restoreEx.getMessage());
                }
                
                // 如果恢复失败，插入新记录
                UserFavoriteMv favorite = new UserFavoriteMv();
                favorite.setUserId(userId);
                favorite.setMvId(mvId);
                favorite.setCreatedAt(LocalDateTime.now());
                
                userFavoriteMvMapper.insert(favorite);
                log.info("添加MV收藏成功: userId={}, mvId={}", userId, mvId);
                return true;
            } else if ("unlike".equals(action)) {
                if (!currentlyFavorited) {
                    // 如果本来就没有收藏，直接返回false，不抛出异常
                    log.info("MV本来就没有收藏，无需取消: userId={}, mvId={}", userId, mvId);
                    return false;
                }
                
                userFavoriteMvMapper.deleteByUserIdAndMvId(userId, mvId);
                log.info("取消MV收藏成功: userId={}, mvId={}", userId, mvId);
                return false;
            }
            throw new RuntimeException("无效的操作类型: " + action);
        } catch (Exception e) {
            log.error("MV收藏操作失败: userId={}, mvId={}, action={}", userId, mvId, action, e);
            throw new RuntimeException("MV收藏操作失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isMvFavorited(Long userId, Long mvId) {
        try {
            Boolean exists = userFavoriteMvMapper.existsByUserIdAndMvId(userId, mvId);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("检查MV收藏状态失败: userId={}, mvId={}", userId, mvId, e);
            return false;
        }
    }

    @Override
    public List<Mv> getUserFavoriteMvs(Long userId) {
        try {
            List<UserFavoriteMv> favorites = userFavoriteMvMapper.selectByUserId(userId);
            List<Mv> mvs = new ArrayList<>();
            for (UserFavoriteMv favorite : favorites) {
                Mv mv = mvMapper.selectById(favorite.getMvId());
                if (mv != null) {
                    mvs.add(mv);
                }
            }
            return mvs;
        } catch (Exception e) {
            log.error("获取用户收藏MV失败: {}", userId, e);
            throw new RuntimeException("获取用户收藏MV失败: " + e.getMessage());
        }
    }
}
