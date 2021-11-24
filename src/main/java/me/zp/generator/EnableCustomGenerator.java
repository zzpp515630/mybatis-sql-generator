package me.zp.generator;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 描述：
 * 2021/5/24 9:06.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(GeneratorConfiguration.class)
public @interface EnableCustomGenerator {

    /**
     * 实体包名
     *
     * @return
     */
    String[] value();

    boolean isDataBase() default true;

    boolean isFile() default false;

    boolean isConnection() default true;
}
