# mybatis-sql-generator
基于mybatis plus注解  sql语句与字段添加反向生成器(支持mysql,sqlite)。
读取实体类型生成sql文件，连接数据库可自动添加缺失字段，缺失表

使用方法：
启动类添加：@EnableCustomGenerator()

可自定义填充属性：
需要继承:me.zp.generator.GenerationCondition
示例：me.zp.generator.TestGeneration
