package com.bc.template.common.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo<T> {

    /**
     * 行数据
     */
    private List<T> rows;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 总行数
     */
    private Integer total;

    public static <T> PageInfo.PageInfoBuilder<T> builder(Class<T> clazz) {
        return new PageInfo.PageInfoBuilder<T>();
    }
}
