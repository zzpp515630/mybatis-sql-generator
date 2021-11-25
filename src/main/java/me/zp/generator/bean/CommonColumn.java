package me.zp.generator.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author zhang
 */
@Data
@NoArgsConstructor
@SuperBuilder
@ToString
public abstract class CommonColumn {

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段长度
     */
    private Integer length;

    /**
     * 字段名
     */
    private String name;

    /**
     * 备注
     */
    private String remarks = "";

    /**
     * decimalLength
     */
    private Integer decimalLength;

    /**
     * 是否为可以为null，true是可以，false是不可以，默认为true
     */
    private boolean nullValue;


    /**
     * 是否是主键，默认false
     */
    private boolean key;


    /**
     * 是否自动递增，默认false 只有主键才能使用
     */
    private boolean autoIncrement;

    /**
     * 默认值，默认为“”
     */
    private String defaultValue;

    /**
     * 排序
     */
    private Integer sort;



}

