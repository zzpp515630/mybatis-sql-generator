package me.zp.generator;

import me.zp.generator.bean.DefaultCommonColumn;
import me.zp.generator.model.GeneratorConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * 2021/5/27 9:30.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
//@Component
public class TestGeneration implements GenerationCondition {


    @Override
    public void column(Map<String, DefaultCommonColumn> columnMap) {
        columnMap.put(Integer.class.getName(), DefaultCommonColumn.builder().length(11).build());
    }

    @Override
    public Map<String, DefaultCommonColumn> filterFields() {
        Map<String, DefaultCommonColumn> map = new HashMap<>(16);
        map.put("appKey", DefaultCommonColumn.builder().remarks("系统key").sort(2).build());
        map.put("updated", DefaultCommonColumn.builder().remarks("修改时间").sort(2).build());
        map.put("created", DefaultCommonColumn.builder().remarks("创建时间").sort(2).build());
        map.put("isDeleted", DefaultCommonColumn.builder().remarks("备注").sort(2).build());
        map.put("version", DefaultCommonColumn.builder().remarks("乐观锁").sort(2).build());
        map.put("companyId", DefaultCommonColumn.builder().remarks("服务机构").sort(2).build());
        return map;
    }

    @Override
    public GeneratorConfig config() {
        return GeneratorConfig.builder().filterTableRemarks("实体类").build();
    }
}
