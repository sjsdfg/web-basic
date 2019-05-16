package cn.sjsdfg.mongo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Joe on 2019/5/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 页码，从1开始
     */
    private Integer pageNum;
    /**
     * 页面大小
     */
    private Integer pageSize;
    /**
     * 总数
     */
    private Long total;
    /**
     * 总页数
     */
    private Integer pages;
    /**
     * 数据
     */
    private List<T> list;
}
