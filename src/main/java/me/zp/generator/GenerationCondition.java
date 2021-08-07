package me.zp.generator;

import me.zp.generator.bean.DefaultCommonColumn;
import me.zp.generator.model.GeneratorConfig;

import java.util.Map;

/**
 * 描述：
 * 2021/5/27 9:27.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
public interface GenerationCondition {

    /**
     * 参数修改
     *
     * @param columnMap
     */
    void column(Map<String, DefaultCommonColumn> columnMap);


    /**
     * 过滤字段
     *
     * @return
     */
    Map<String, DefaultCommonColumn> filterFields();


    /**
     * 其他配置
     *
     * @return
     */
    GeneratorConfig config();
}
