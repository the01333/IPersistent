package com.puxinxiaolin.executor;

import com.puxinxiaolin.config.BoundSql;
import com.puxinxiaolin.pojo.Configuration;
import com.puxinxiaolin.pojo.MappedStatement;
import com.puxinxiaolin.utils.GenericTokenParser;
import com.puxinxiaolin.utils.ParameterMapping;
import com.puxinxiaolin.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object param) throws Exception {
        // 1. 加载驱动，获取数据库连接
        connection = configuration.getDataSource().getConnection();

        // 2. 获取 PreparedStatement 预编译对象
        String sql = mappedStatement.getSql();
        // 替换占位符为 ?，并且保存占位符里的值
        BoundSql boundSql = getBoundSql(sql);
        String finalSql = boundSql.getFinalSql();
        preparedStatement = connection.prepareStatement(finalSql);

        // 3. 设置参数
        String parameterType = mappedStatement.getParameterType();
        if (parameterType != null) {
            Class<?> clazz = Class.forName(parameterType);

            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String parameter = parameterMapping.getContent();
                Field field = clazz.getDeclaredField(parameter);
                // 有可能是 private 修饰的，需要设置可访问性
                field.setAccessible(true);

                Object value = field.get(param);
                // 赋值占位符，PreparedStatement 的下标是从 1 开始的
                preparedStatement.setObject(i + 1, value);
                field.setAccessible(false);
            }
        }

        // 4. 执行 sql
        resultSet = preparedStatement.executeQuery();

        // 5. 处理返回结果集
        List<E> result = new ArrayList<>();
        while (resultSet.next()) {
            // 元数据信息（内含字段名和字段值）
            ResultSetMetaData metaData = resultSet.getMetaData();

            String resultType = mappedStatement.getResultType();
            Class<?> resultClazz = Class.forName(resultType);
            Object o = resultClazz.newInstance();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(columnName);

                // 封装返回实体
                // 属性描述器（内含 API 可以实现对某个属性的读写）
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultClazz);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, columnValue);
            }
            result.add((E) o);
        }

        return result;
    }

    /**
     * 关闭资源
     */
    @Override
    public void close() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 替换占位符为 ?，并且保存占位符里的值
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        // 1. 创建标记处理器，配合标记解析器完成标记的处理解析工作
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler();

        // 2. 创建标记解析器
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);

        // 3. #{} -> ?
        String finalSql = parser.parse(sql);
        // #{} 里的值的集合
        List<ParameterMapping> parameterMappings = handler.getParameterMappings();
        return new BoundSql(finalSql, parameterMappings);
    }

}
