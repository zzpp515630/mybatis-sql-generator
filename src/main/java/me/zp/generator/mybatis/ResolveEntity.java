package me.zp.generator.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import me.zp.generator.annotation.Column;
import me.zp.generator.bean.CommonColumn;
import me.zp.generator.bean.DefaultCommonColumn;
import me.zp.generator.utils.NameConversionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 描述：
 * 2021/5/27 9:03.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
public class ResolveEntity {

    private final Map<String, Map<String, String>> remarks;

    private final Map<String, DefaultCommonColumn> commonColumnMap;

    private final Map<String, DefaultCommonColumn> fieldCommonMap;


    public ResolveEntity(Map<String, DefaultCommonColumn> fieldCommonMap,
                         Map<String, DefaultCommonColumn> commonColumnMap,
                         Map<String, Map<String, String>> remarks) {
        this.remarks = remarks;
        this.commonColumnMap = commonColumnMap;
        this.fieldCommonMap = fieldCommonMap;
    }


    public String getTableName(Class<?> aClass) {
        boolean annotation = aClass.isAnnotationPresent(TableName.class);
        if (annotation) {
            TableName tableName = aClass.getAnnotation(TableName.class);
            return tableName.value();
        }
        //读取文件名称 自动构建
        return null;
    }

    /**
     * 迭代出所有model的所有fields存到newFieldList中
     *
     * @param aClass 准备做为创建表依据的class
     * @param aClass
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<CommonColumn> tableFieldsConstruct(Class<?> aClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Set<CommonColumn> newFieldList = new HashSet<>();
        // 判断是否有父类，如果有拉取父类的field，这里只支持多层继承
        Field[] fields = recursionParents(aClass, aClass.getDeclaredFields());
        for (Field field : fields) {
            resolveEntity(aClass, field, newFieldList);
        }
        return new ArrayList<>(newFieldList);
    }


    /**
     * 构建增加的删除的修改的字段
     *
     * @param newFieldList 用于存新增表的字段
     * @param columnNames  从sysColumns中取出我们需要比较的列的List
     */
    public List<CommonColumn> buildAddFields(List<String> columnNames, List<CommonColumn> newFieldList) {
        Set<CommonColumn> addFieldList = new HashSet<>();
        for (CommonColumn commonColumn : newFieldList) {
            if (!this.isExistField(columnNames, commonColumn)) {
                addFieldList.add(commonColumn);
            }
        }
        return new ArrayList<>(addFieldList);
    }

    /**
     * 判断字段是否已经存在了
     *
     * @param columnNames
     * @param commonColumn
     * @return
     */
    private boolean isExistField(List<String> columnNames, CommonColumn commonColumn) {
        for (String columnName : columnNames) {
            if (commonColumn.getName().equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 递归扫描父类的fields
     *
     * @param aClass 类
     * @param fields 属性
     */
    private Field[] recursionParents(Class<?> aClass, Field[] fields) {
        if (aClass.getSuperclass() != null) {
            Class<?> clsSup = aClass.getSuperclass();
            fields = ArrayUtils.addAll(fields, clsSup.getDeclaredFields());
            fields = recursionParents(clsSup, fields);
        }
        return fields;
    }


    private void resolveEntity(Class<?> aClass, Field field, Set<CommonColumn> newFieldList) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Column column = field.getAnnotation(Column.class);
        // 注解，不需要的字段
        if (column != null && column.isIgnore()) {
            return;
        }
        //mybatis
        TableField tableField = field.getAnnotation(TableField.class);
        if (null != tableField && !tableField.exist()) {
            return;
        }
        //如果是静态类则跳过
        boolean aStatic = Modifier.isStatic(field.getModifiers());
        if (aStatic) {
            return;
        }
        //通过工厂获取   //创建实体
        CommonColumn commonColumn = getCommonColumn(field);

        //column
        columnData(column, field, commonColumn);
        //mybatis plus
        tableFieldData(tableField, field, commonColumn);
        //添加备注
        addRemarks(aClass.getSimpleName(), field, commonColumn);

        if (StringUtils.isBlank(commonColumn.getName())) {
            String fieldName = NameConversionUtils.humpToLine2(field.getName());
            commonColumn.setName(fieldName);
        }
        newFieldList.add(commonColumn);
    }


    private void addRemarks(String className, Field field, CommonColumn commonColumn) {
        //添加备注
        Map<String, String> remarkMap = remarks.get(className);
        if (null != remarkMap) {
            String remarks = remarkMap.get(field.getName());
            commonColumn.setRemarks(remarks);
        }
    }

    private void tableFieldData(TableField tableField, Field field, CommonColumn commonColumn) {
        if (null != tableField && StringUtils.isBlank(tableField.value())) {
            commonColumn.setName(tableField.value());
        }
        TableId annotation = field.getAnnotation(TableId.class);
        if (null != annotation) {
            commonColumn.setKey(true);
            int key = annotation.type().getKey();
            commonColumn.setAutoIncrement(IdType.AUTO.getKey() == key);
        }
    }

    private void columnData(Column column, Field field, CommonColumn commonColumn) {
        //设置字段名称
        if (column != null && StringUtils.isBlank(column.name())) {
            commonColumn.setName(column.name());
        }
        //设置字段类型
        if (column != null && StringUtils.isBlank(column.type())) {
            commonColumn.setType(column.type());
        }

        //设置长度
        if (column != null && column.length() > 0) {
            commonColumn.setLength(column.length());
        }
        //设置长度
        if (column != null && column.length() > 0) {
            commonColumn.setDecimalLength(column.decimalLength());
        }
        //设置自增
        if (column != null && column.isAutoIncrement()) {
            commonColumn.setAutoIncrement(column.isAutoIncrement());
        }
        //设置自增
        if (column != null && column.isAutoIncrement()) {
            commonColumn.setAutoIncrement(column.isAutoIncrement());
        }
        //是否为空
        if (column != null && !column.isNull()) {
            commonColumn.setNullValue(column.isNull());
        }
        //是否主键
        if (column != null && !column.isKey()) {
            commonColumn.setKey(column.isKey());
        }
        //设置字段类型
        if (column != null && StringUtils.isBlank(column.defaultValue())) {
            commonColumn.setDefaultValue(column.defaultValue());
        }
    }


    public CommonColumn getCommonColumn(Field field) {
        DefaultCommonColumn defaultCommonColumn = commonColumnMap.get(field.getType().getName());
        DefaultCommonColumn fieldCommon = fieldCommonMap.get(field.getName());
        if (null == fieldCommon) {
            fieldCommon = new DefaultCommonColumn();
        }
        if (null == defaultCommonColumn) {
            defaultCommonColumn = commonColumnMap.get("java.lang.String");
        }
        DefaultCommonColumn commonColumn = DefaultCommonColumn.builder().build();
        commonColumn.setLength(Optional.ofNullable(fieldCommon.getLength()).orElse(defaultCommonColumn.getLength()));
        commonColumn.setDecimalLength(Optional.ofNullable(fieldCommon.getDecimalLength()).orElse(defaultCommonColumn.getDecimalLength()));
        commonColumn.setDefaultValue(Optional.ofNullable(fieldCommon.getDefaultValue()).orElse(defaultCommonColumn.getDefaultValue()));
        commonColumn.setName(Optional.ofNullable(fieldCommon.getName()).orElse(defaultCommonColumn.getName()));
        commonColumn.setRemarks(Optional.ofNullable(fieldCommon.getRemarks()).orElse(defaultCommonColumn.getRemarks()));
        commonColumn.setSort(Optional.ofNullable(fieldCommon.getSort()).orElse(defaultCommonColumn.getSort()));
        commonColumn.setType(Optional.ofNullable(fieldCommon.getType()).orElse(defaultCommonColumn.getType()));
        commonColumn.setAutoIncrement(BooleanUtils.isTrue(fieldCommon.isAutoIncrement()));
        commonColumn.setKey(BooleanUtils.isTrue(fieldCommon.isKey()));
        commonColumn.setNullValue(BooleanUtils.isTrue(fieldCommon.isNullValue()));
        return commonColumn;
    }


}
