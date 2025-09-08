package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.ArtistTimeline;

import java.util.List;

/**
 * 歌手时间线Mapper接口
 */
@Mapper
public interface ArtistTimelineMapper {
    
    /**
     * 根据ID查询歌手时间线
     */
    ArtistTimeline selectById(@Param("id") Long id);
    
    /**
     * 查询所有歌手时间线
     */
    List<ArtistTimeline> selectAll();
    
}