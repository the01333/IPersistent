package com.puxinxiaolin.config;

import com.puxinxiaolin.pojo.Configuration;
import com.puxinxiaolin.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @Description: 解析 mapper.xml 文件的解析器
 * @Author: YCcLin
 * @Date: 2025/4/21 17:00
 */
public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream mapperStream) throws DocumentException {
        Document document = new SAXReader().read(mapperStream);
        Element rootElement = document.getRootElement();
        List<Element> selectList = rootElement.selectNodes("//select");
        String namespace = rootElement.attributeValue("namespace");

        for (Element element : selectList) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");
            String sql = element.getTextTrim();

            MappedStatement mappedStatement = new MappedStatement();
            String statementId = namespace + "." + id;
            mappedStatement.setStatementId(statementId);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setSql(sql);

            configuration.getMappedStatementMap().put(statementId, mappedStatement);
        }
    }

}
