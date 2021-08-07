package me.zp.generator.mapper;

import me.zp.generator.model.TableSql;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * sql生成类
 *
 * @author zzpp
 */
public interface SqlGeneratorMapper {

    /**
     * 根据表名查询表在库中是否存在，存在返回1，不存在返回0
     *
     * @param tableName
     * @return
     */
    @Select("select count(1) from information_schema.tables where table_name = #{tableName} and table_schema = (select database())")
    int findTableCountByTableName(@Param("tableName") String tableName);

    /**
     * 根据表名查询库中该表的字段结构等信息
     *
     * @param tableName
     * @return
     */
    @Select("select column_name  from information_schema.columns where table_name = #{tableName} and table_schema = (select database())")
    List<String> findTableEnsembleByTableName(@Param("tableName") String tableName);

    /**
     * 增加字段
     *
     * @param tableSql
     */
    @Insert("<script>" +
            "<foreach collection=\"columnSqls\" index=\"key\" item=\"fields\" separator=\";\" close=\";\">" +
            "    alter table `${name}` add `${fields.fieldName}` ${fields.fieldType}" +
            "</foreach>"
            + "</script>")
    void addTableField(TableSql tableSql);

    /**
     * 根据结构注解解析出来的信息创建表
     *
     * @param tableSql
     */
    @Insert("<script>" +
            "create table `${name}`(" +
            "<foreach collection=\"columnSqls\" item=\"fields\" separator=\",\">`${fields.fieldName}` ${fields.fieldType}</foreach>" +
            "<if test=\"primaryKey != null and primaryKey != ''\">,PRIMARY KEY (`${primaryKey}`)</if>)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;"
            + "</script>")
    void createTable(TableSql tableSql);

    /**
     * 根据表名删除表
     *
     * @param tableName 表结构的map
     */
    @Delete("DROP TABLE IF EXISTS `${tableName}`;")
    void dropTableByName(@Param("tableName") String tableName);


    /**
     * 根据表名删除表
     *
     * @param table 表结构的map
     */
    @Insert("${table}")
    void table(@Param("table") String table);


}
