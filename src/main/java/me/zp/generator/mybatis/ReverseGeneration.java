package me.zp.generator.mybatis;

import lombok.extern.slf4j.Slf4j;
import me.zp.generator.ColumnFactory;
import me.zp.generator.GeneratorConfiguration;
import me.zp.generator.bean.CommonColumn;
import me.zp.generator.bean.DefaultCommonColumn;
import me.zp.generator.mapper.SqlGeneratorMapper;
import me.zp.generator.model.CommonField;
import me.zp.generator.service.SqlSessionService;
import me.zp.generator.utils.ClassScanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author zzpp
 */
@Slf4j
@Component
public class ReverseGeneration {

    /**
     * 备注
     */
    private static final Map<String, Map<String, String>> REMARKS = new HashMap<>(16);

    /**
     * 表名
     */
    private static final Map<String, String> TABLE_NAMES = new HashMap<>(16);

    /**
     * 用于存需要创建的表名+结构
     */
    private static final Map<String, List<CommonColumn>> NEW_TABLE_MAP = new HashMap<>(16);
    /**
     * 用于存需要增加字段的表名+结构
     */
    private static final Map<String, List<CommonColumn>> ADD_TABLE_MAP = new HashMap<>(16);


    /**
     * 用于存需要创建的表名+结构
     */
    private static final List<CommonField> NEW_TABLE_LIST = new ArrayList<>();
    /**
     * 用于存需要增加字段的表名+结构
     */
    private static final List<CommonField> ADD_TABLE_LIST = new ArrayList<>();

    private Set<Class<?>> aClass;

    @Resource
    private SqlSessionService sqlSessionService;

    @Resource
    private ColumnFactory columnFactory;


    @PostConstruct
    public void initGeneration() {
        try {
            //读取class
            aClass = ClassScanUtils.getClass(GeneratorConfiguration.PACKAGE_PATH);
            //读取字段字段备注
            remark(GeneratorConfiguration.SOURCE_ENTITY_PATH);
            //收集数据
            allTableMapConstruct();
            //生成sql文件
            generateSqlFile(GeneratorConfiguration.SOURCE_PATH);
            //创建或修改表
            createOrModifyTableConstruct();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    private void remark(String sourceEntityPath) throws IOException {
        RemarksGeneration remarksGeneration = new RemarksGeneration(TABLE_NAMES, REMARKS);
        remarksGeneration.remark(sourceEntityPath, aClass);
    }

    private void generateSqlFile(String sourcePath) throws IOException {
        if (!GeneratorConfiguration.IS_SQL_FILE) {
            return;
        }
        SqlFileGeneration sqlFileGeneration = new SqlFileGeneration(NEW_TABLE_LIST, ADD_TABLE_LIST);
        sqlFileGeneration.generateSqlFile(sourcePath);
    }


    /**
     * 构建出全部表的增删改的map
     */
    private void allTableMapConstruct() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Map<String, DefaultCommonColumn> commonColumnMap = columnFactory.getCommonColumnMap();
        Map<String, DefaultCommonColumn> fieldCommonMap = columnFactory.getFieldCommonMap();
        ResolveEntity resolveEntity = new ResolveEntity(fieldCommonMap, commonColumnMap, REMARKS);
        for (Class<?> aClass : aClass) {
            //获取表名称
            String tableName = resolveEntity.getTableName(aClass);
            if (StringUtils.isBlank(tableName)) {
                continue;
            }
            String tableRemarks = TABLE_NAMES.get(aClass.getSimpleName());
            // 用于存新增表的字段 迭代出所有model的所有fields存到newFieldList中
            List<CommonColumn> newFieldList = resolveEntity.tableFieldsConstruct(aClass);

            // 先查该表是否以存在
            int exist = sqlSessionService.getMapper(SqlGeneratorMapper.class).findTableCountByTableName(tableName);

            // 不存在时
            if (exist == 0) {
                NEW_TABLE_LIST.add(CommonField.builder().tableName(tableName).remarks(tableRemarks).columnList(newFieldList).build());
                continue;
            }
            // 已存在时理论上做修改的操作，这里查出该表的结构
            List<String> columnNames = sqlSessionService.getMapper(SqlGeneratorMapper.class).findTableEnsembleByTableName(tableName);
            // 验证对比从model中解析的fieldList与从数据库查出来的columnList
            // 1. 找出增加的字段
            List<CommonColumn> addFieldList = resolveEntity.buildAddFields(columnNames, newFieldList);
            //如果有值则添加map
            if (!CollectionUtils.isEmpty(addFieldList)) {
                ADD_TABLE_LIST.add(CommonField.builder().tableName(tableName).remarks(tableRemarks).columnList(addFieldList).build());
            }
        }
    }


    /**
     * 根据传入的map创建或修改表结构
     */
    private void createOrModifyTableConstruct() {
        if (!GeneratorConfiguration.IS_SQL_DB) {
            return;
        }
        // 1. 创建表
        for (CommonField commonField : NEW_TABLE_LIST) {
            List<String> createTable = commonField.createTableSql();
            String collect = String.join("", createTable);
            sqlSessionService.getMapper(SqlGeneratorMapper.class).table(collect);
        }
        // 2. 添加新的字段
        for (CommonField commonField : ADD_TABLE_LIST) {
            List<String> addFieldsSql = commonField.addFieldsSql();
            for (String sql : addFieldsSql) {
                sqlSessionService.getMapper(SqlGeneratorMapper.class).table(sql);
            }
        }
    }

}
