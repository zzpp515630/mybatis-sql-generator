package me.zp.generator.model;

import lombok.Data;

import java.util.List;

/**
 * @author zzpp
 */
@Data
public class TableSql {

  /**
   * 名称
   */
  private String name;

  /**
   * 主键
   */
  private List<String> primaryKey;

  /**
   * 备注
   */
  private String remarks = "";

  /**
   * 工程
   */
  private List<ColumnSql> columnSqlList;

}
