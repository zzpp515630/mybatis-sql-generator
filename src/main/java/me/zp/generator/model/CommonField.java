package me.zp.generator.model;

import lombok.Builder;
import lombok.Data;
import me.zp.generator.bean.CommonColumn;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：
 * 2021/5/27 16:31.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
public class CommonField {

    /**
     * 名称
     */
    private String tableName;

    /**
     * 主键
     */
    private String remarks;


    private List<CommonColumn> columnList;


    private TableSql getTableSql() {
        TableSql tableSql = new TableSql();
        tableSql.setName(tableName);
        tableSql.setRemarks(remarks);
        List<String> primaryKeys = getPrimaryKey(columnList);
        List<ColumnSql> columnSqlList = new ArrayList<>();
        for (CommonColumn column : columnList) {
            columnSqlList.add(new ColumnSql(column, primaryKeys));
        }
        columnSqlList.sort(Comparator.comparingInt(ColumnSql::getSort));
        tableSql.setPrimaryKey(primaryKeys);
        tableSql.setColumnSqlList(columnSqlList);
        return tableSql;
    }

    private static List<String> getPrimaryKey(List<CommonColumn> columns) {
        return columns.stream().filter(CommonColumn::isKey).map(CommonColumn::getName).collect(Collectors.toList());
    }


    public List<String> addFieldsSql() {
        List<String> array = new ArrayList<>();
        String start = " alter table `${name}` add `${fieldName}` ${fieldType};";
        TableSql tableSql = getTableSql();
        for (ColumnSql columnSql : tableSql.getColumnSqlList()) {
            String sql = start.replace("${name}", tableSql.getName())
                    .replace("${fieldName}", columnSql.getFieldName())
                    .replace("${fieldType}", columnSql.getFieldType());
            array.add(sql);
        }
        return array;
    }

    public List<String> createTableSql() {
        List<String> array = new ArrayList<>();
        String start = "create table `${name}`(";
        String type = " `${fieldName}` ${fieldType}";
        String primaryKey = ",PRIMARY KEY (${primaryKey})";
        String end = ")ENGINE=INNODB DEFAULT CHARSET=utf8mb4;";
        String endComment = ")ENGINE=INNODB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='${comment}';";
        TableSql tableSql = getTableSql();
        List<ColumnSql> columnSqlList = tableSql.getColumnSqlList();
        List<String> primaryKeyValue = tableSql.getPrimaryKey();
        array.add(start.replace("${name}", tableSql.getName()));
        for (int i = 0; i < columnSqlList.size(); i++) {
            ColumnSql columnSql = columnSqlList.get(i);
            String fieldName = columnSql.getFieldName();
            String fieldType = columnSql.getFieldType();
            String replace = type.replace("${fieldName}", fieldName).replace("${fieldType}", fieldType);
            if (columnSqlList.size() - 1 == i) {
                array.add(replace);
            } else {
                array.add(replace + ",");
            }
        }
        if (!CollectionUtils.isEmpty(primaryKeyValue)) {
            array.add(primaryKey.replace("${primaryKey}", primaryKeyValue.stream().collect(Collectors.joining("`,`", "`", "`"))));
        }
        if (StringUtils.isNotBlank(tableSql.getRemarks())) {
            array.add(endComment.replace("${comment}", tableSql.getRemarks()));
        } else {
            array.add(end);
        }
        array.add("\r");
        return array;
    }


}
