package me.zp.generator.service;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：
 * 2021/5/26 21:25.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
public class SqlSessionService implements InitializingBean {

    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.addMappers("me.zp.generator.mapper");
    }


    public <T> T getMapper(Class<T> tClass) throws BeansException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(tClass);
    }


}
