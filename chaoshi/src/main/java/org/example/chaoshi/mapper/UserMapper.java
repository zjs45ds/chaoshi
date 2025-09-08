package org.example.chaoshi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.chaoshi.entity.User;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 查询所有用户
     */
    List<User> selectAll();
    
    /**
     * 插入用户
     */
    int insertUser(User user);
    
    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(@Param("email") String email);
    
    /**
     * 分页查询用户
     */
    List<User> selectPage(@Param("username") String username,
                         @Param("email") String email,
                         @Param("offset") Long offset, 
                         @Param("size") Long size);
    
    /**
     * 统计用户数量
     */
    Long countUsers(@Param("username") String username,
                    @Param("email") String email);
    
    /**
     * 更新用户信息
     */
    int updateUser(User user);
    
    /**
     * 逻辑删除用户
     */
    int deleteUser(@Param("id") Long id);
    
    /**
     * 检查用户名是否存在
     */
    Boolean existsByUsername(@Param("username") String username);
    
    /**
     * 检查邮箱是否存在
     */
    Boolean existsByEmail(@Param("email") String email);
}