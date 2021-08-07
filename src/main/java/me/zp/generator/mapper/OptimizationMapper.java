package me.zp.generator.mapper;

import me.zp.generator.optimization.OptimizationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OptimizationMapper {


    /**
     * 根据表名查询库中该表的字段结构等信息
     *
     * @param select
     * @return
     */
    @Select("explain ${select}")
    List<OptimizationVo> findExplain(@Param("select") String select);

}
