package me.zp.generator;

import me.zp.generator.bean.DefaultCommonColumn;
import me.zp.generator.utils.CustomOptional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 字段类注册到工厂
 *
 * @author zhang
 */
@Component
public class ColumnFactory {

    private Map<String, DefaultCommonColumn> columnMap = new HashMap<>();

    /**
     *
     */
    private Map<String, DefaultCommonColumn> fieldCommonMap = new HashMap<>();


    @Autowired(required = false)
    public ColumnFactory(GenerationCondition generationCondition) {
        init();
        generationCondition.column(columnMap);
        CustomOptional.of(generationCondition.filterFields()).isPresent(fieldCommonMap::putAll);
    }

    public ColumnFactory() {
        init();
    }


    public void init() {
        columnMap.put(Date.class.getName(), DefaultCommonColumn.builder().type("datetime").length(0).sort(99).build());
        columnMap.put(LocalDateTime.class.getName(), DefaultCommonColumn.builder().type("datetime").length(0).sort(99).build());

        columnMap.put(Double.class.getName(), DefaultCommonColumn.builder().type("double").length(10).decimalLength(2).sort(99).build());

        columnMap.put(Integer.class.getName(), DefaultCommonColumn.builder().type("int").length(10).sort(99).build());
        columnMap.put(int.class.getName(), DefaultCommonColumn.builder().type("int").length(10).sort(99).build());

        columnMap.put(Long.class.getName(), DefaultCommonColumn.builder().type("bigint").length(20).sort(99).build());
        columnMap.put(long.class.getName(), DefaultCommonColumn.builder().type("bigint").length(20).sort(99).build());

        columnMap.put(Boolean.class.getName(), DefaultCommonColumn.builder().type("tinyint").length(10).sort(99).build());
        columnMap.put(boolean.class.getName(), DefaultCommonColumn.builder().type("tinyint").length(10).sort(99).build());

        columnMap.put(String.class.getName(), DefaultCommonColumn.builder().type("varchar").length(255).sort(99).build());
        columnMap.put(BigDecimal.class.getName(), DefaultCommonColumn.builder().type("decimal").length(10).decimalLength(2).sort(99).build());
    }

    public Map<String, DefaultCommonColumn> getCommonColumnMap() {
        return columnMap;
    }


    public Map<String, DefaultCommonColumn> getFieldCommonMap() {
        return fieldCommonMap;
    }
}
