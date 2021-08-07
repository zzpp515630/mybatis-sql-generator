package me.zp.generator.model;

import lombok.Data;
import me.zp.generator.bean.CommonColumn;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author zzpp
 */
@Data
public class ColumnSql {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 主键
     */
    private String primaryKey;

    /**
     * 自定义排序
     */
    private int sort;


    public ColumnSql(CommonColumn column, List<String> primaryKeys) {
        //排序
        this.sort = column.getSort();
        //字段名称
        this.fieldName = column.getName();
        if (primaryKeys.size() > 0) {
            this.primaryKey = primaryKeys.toString().replace("[", "").replace("]", "");
        }
        if (column.isKey()) {
            this.sort = 1;
        }

        //数据生成
        this.fieldType = getFieldType(column);

    }

    private String getFieldType(CommonColumn column) {
        StringBuilder stringBuilder = new StringBuilder();
        //类型加长度
        stringBuilder.append(column.getType());
        if (null != column.getDecimalLength()) {
            stringBuilder.append("(").append(column.getLength()).append(",").append(column.getDecimalLength()).append(")");
        } else {
            stringBuilder.append("(").append(column.getLength()).append(")");
        }
        //不是NULL
        stringBuilder.append(" ");
        if (column.isKey() && column.isAutoIncrement()) {
            stringBuilder.append("NOT NULL").append(" ").append("AUTO_INCREMENT").append(" ");
        } else if (column.isKey()) {
            stringBuilder.append("NOT NULL").append(" ");
        } else {
            stringBuilder.append("DEFAULT").append(" ").append(Optional.ofNullable(column.getDefaultValue()).orElse("NULL"));
        }

        stringBuilder.append(" ");
        //备注
        if (StringUtils.isNotBlank(column.getRemarks())) {
            stringBuilder.append("COMMENT").append(" ").append("\"").append(column.getRemarks()).append("\"");
        } else {
            stringBuilder.append("COMMENT").append(" ").append("\"\"");
        }
        return stringBuilder.toString();
    }

}
