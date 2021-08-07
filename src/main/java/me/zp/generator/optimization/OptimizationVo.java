package me.zp.generator.optimization;

import lombok.Data;

/**
 * 优化返回字段
 * @author zhang peng
 */
@Data
public class OptimizationVo {
    /**
     * 顺序
     */
    private Long id;

    /**
     * 类型
     */
    private String selectType;

    /**
     * 表名
     */
    private String table;

    /**
     * 分区
     */
    private String partitions;

    /**
     * 查询类型
     */
    private String type;

    /**
     * 预计使用索引
     */
    private String possibleKeys;

    /**
     * 使用的索引
     */
    private String key;

    /**
     * 索引长度
     */
    private String keyLen;

    /**
     *
     */
    private String ref;

    /**
     * 检索行数
     */
    private String rows;

    /**
     *
     */
    private String filtered;

    /**
     *
     */
    private String extra;

}
