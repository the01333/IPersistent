package com.puxinxiaolin.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.puxinxiaolin.io.Resources;
import com.puxinxiaolin.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @Description: 解析核心配置文件的解析器
 * @Author: YCcLin
 * @Date: 2025/4/21 17:00
 */
public class XMLConfigBuilder {

    private Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    /**
     * 使用 dom4j + xpath 解析配置文件，封装 Configuration 对象
     *
     * @param inputStream
     * @return
     */
    public Configuration parse(InputStream inputStream) throws DocumentException {
        // 1. 解析核心配置文件
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//property");

        Properties properties = new Properties();
        for (Element element : list) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        }

        // 创建数据源对象
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(properties.getProperty("driverClassName"));
        druidDataSource.setUrl(properties.getProperty("url"));
        druidDataSource.setUsername(properties.getProperty("username"));
        druidDataSource.setPassword(properties.getProperty("password"));
        configuration.setDataSource(druidDataSource);

        // 2. 解析映射配置文件
        List<Element> mapperList = rootElement.selectNodes("//mapper");
        for (Element element : mapperList) {
            String mapperPath = element.attributeValue("resource");

            InputStream mapperStream = Resources.getResourceAsStream(mapperPath);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parse(mapperStream);
        }

        return configuration;
    }

}
