package me.zp.generator;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 描述：
 * 2021/5/24 9:15.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class GeneratorConfiguration implements ImportBeanDefinitionRegistrar {

    /**
     * 源码地址
     */
    public static String SOURCE_PATH;
    /**
     * 源码实体地址
     */
    public static String SOURCE_ENTITY_PATH;

    /**
     * 包路径
     */
    public static String PACKAGE_PATH;

    /**
     * 是否输出sql文件
     */
    public static boolean IS_SQL_FILE;

    /**
     * 是否输出sql文件
     */
    public static boolean IS_SQL_DB;

    /**
     * 是否连接数据库
     */
    public static boolean IS_CONNECTION;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableCustomGenerator.class.getName());
        assert annotationAttributes != null;
        Object entityPackages = annotationAttributes.get("value");
        String[] paths = (String[]) entityPackages;
        String packageName = paths[0];
        conversionPath(packageName);
        GeneratorConfiguration.IS_SQL_DB = (boolean) annotationAttributes.get("isDataBase");
        GeneratorConfiguration.IS_SQL_FILE = (boolean) annotationAttributes.get("isFile");
        GeneratorConfiguration.IS_CONNECTION = (boolean) annotationAttributes.get("isConnection");


        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Autowired.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));
        scanner.scan("me.zp.generator");

    }


    public static void conversionPath(String packageName) {
        PACKAGE_PATH = packageName;
        String path = EnableCustomGenerator.class.getResource("/").getPath();
        SOURCE_PATH = path.substring(0, path.indexOf("target"));
        SOURCE_ENTITY_PATH = SOURCE_PATH + "src/main/java/" + PACKAGE_PATH.replace(".", "/") + "/";
    }

}
