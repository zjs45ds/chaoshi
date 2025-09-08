package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.Mv;

import java.util.List;

/**
 * MV Mapper接口
 */
@Mapper
public interface MvMapper {
    
    /**
     * 根据ID查询MV
     */
    Mv selectById(@Param("id") Long id);
    
    /**
     * 查询所有MV
     */
    List<Mv> selectAll();
    
    /**
     * 插入MV
     */
    int insertMv(Mv mv);
    
    /**
     * 批量插入MV
     */
    int insertBatch(@Param("list") List<Mv> mvs);
    
    /**
     * 根据歌手ID查询MV列表
     */
    List<Mv> selectByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 分页查询MV
     */
    List<Mv> selectPage(@Param("name") String name, 
                       @Param("artistId") Long artistId,
                       @Param("offset") Long offset, 
                       @Param("size") Long size);
    
    /**
     * 统计MV数量
     */
    Long countMvs(@Param("name") String name, 
                  @Param("artistId") Long artistId);
    
    /**
     * 更新MV信息
     */
    int updateMv(Mv mv);
    
    /**
     * 逻辑删除MV
     */
    int deleteMv(@Param("id") Long id);
    
    /**
     * 批量逻辑删除MV
     */
    int deleteBatch(@Param("list") List<Long> ids);
    
    /**
     * 增加播放次数
     */
    int incrementPlayCount(@Param("id") Long id);
    
    /**
     * 搜索MV
     */
    List<Mv> searchMvs(@Param("keyword") String keyword, 
                      @Param("offset") Long offset, 
                      @Param("size") Long size);
    
    /**
     * 获取热门MV
     */
    List<Mv> selectPopularMvs(@Param("limit") Integer limit);
    
    /**
     * 获取最新MV
     */
    List<Mv> selectLatestMvs(@Param("limit") Integer limit);
}