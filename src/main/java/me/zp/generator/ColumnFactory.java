package me.zp.generator;

import me.zp.generator.bean.DefaultCommonColumn;
import me.zp.generator.utils.CustomOptional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 字段类注册到工厂
 *
 * @author zhang
 */
@Component
public class ColumnFactory {

    private final Map<String, DefaultCommonColumn> columnMap = new HashMap<>(16);

    /**
     *
     */
    private Map<String, DefaultCommonColumn> fieldCommonMap = new HashMap<>(16);


    @Autowired(required = false)
    public ColumnFactory(GenerationCondition generationCondition) {
        init();
        initCustomType(generationCondition);

        CustomOptional.of(generationCondition.filterFields()).isPresent(fieldCommonMap::putAll);
    }

    private void initCustomType(GenerationCondition generationCondition) {
        Map<String, DefaultCommonColumn> customColumnMap = new HashMap<>(16);
        generationCondition.column(customColumnMap);
        if (!CollectionUtils.isEmpty(customColumnMap)) {
            for (Map.Entry<String, DefaultCommonColumn> entry : customColumnMap.entrySet()) {
                DefaultCommonColumn defaultCommonColumn = this.columnMap.get(entry.getKey());
                if (null == defaultCommonColumn) {
                    continue;
                }
                DefaultCommonColumn commonColumn = entry.getValue();
                commonColumn.setLength(Optional.ofNullable(commonColumn.getLength()).orElse(defaultCommonColumn.getLength()));
                commonColumn.setDecimalLength(Optional.ofNullable(commonColumn.getDecimalLength()).orElse(defaultCommonColumn.getDecimalLength()));
                commonColumn.setDefaultValue(Optional.ofNullable(commonColumn.getDefaultValue()).orElse(defaultCommonColumn.getDefaultValue()));
                commonColumn.setName(Optional.ofNullable(commonColumn.getName()).orElse(defaultCommonColumn.getName()));
                commonColumn.setRemarks(Optional.ofNullable(commonColumn.getRemarks()).orElse(defaultCommonColumn.getRemarks()));
                commonColumn.setSort(Optional.ofNullable(commonColumn.getSort()).orElse(defaultCommonColumn.getSort()));
                commonColumn.setType(Optional.ofNullable(commonColumn.getType()).orElse(defaultCommonColumn.getType()));
                commonColumn.setAutoIncrement(BooleanUtils.isTrue(commonColumn.isAutoIncrement()));
                commonColumn.setKey(BooleanUtils.isTrue(commonColumn.isKey()));
                commonColumn.setNullValue(BooleanUtils.isTrue(commonColumn.isNullValue()));
            }
        }

        this.columnMap.putAll(customColumnMap);
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
