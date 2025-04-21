package com.puxinxiaolin.sqlSession;

import com.puxinxiaolin.config.XMLConfigBuilder;
import com.puxinxiaolin.pojo.Configuration;
import org.dom4j.DocumentException;

import java.io.InputStream;

/**
 * @Description: 用于创建 SqlSession（内含执行 sql 所需的所有方法）的工厂
 * @Author: YCcLin
 * @Date: 2025/4/21 17:05
 */
public class SqlSessionFactoryBuilder {

    /**
     * 1. 解析配置文件，封装容器对象
     * 2. 创建 SqlSessionFactory 对象
     *
     * @param inputStream
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream) throws DocumentException {
        // 1. 解析配置文件，封装容器对象 XMLConfigBuilder（解析核心配置文件）
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parse(inputStream);

        // 2. 创建 SqlSessionFactory 工厂对象
        return new DefaultSqlSessionFactory(configuration);
    }

}
