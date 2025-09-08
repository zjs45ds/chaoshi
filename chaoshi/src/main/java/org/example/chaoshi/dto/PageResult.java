package org.example.chaoshi.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页响应结果类
 */
@Data
public class PageResult<T> {
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 数据列表
     */
    private List<T> content;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    public PageResult() {}
    
    public PageResult(Long total, List<T> content, Integer page, Integer size) {
        this.total = total;
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
    }
    
    public static <T> PageResult<T> of(Long total, List<T> content, Integer page, Integer size) {
        return new PageResult<>(total, content, page, size);
    }
}