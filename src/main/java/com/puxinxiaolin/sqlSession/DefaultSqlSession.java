package com.puxinxiaolin.sqlSession;

import com.puxinxiaolin.executor.Executor;
import com.puxinxiaolin.pojo.Configuration;
import com.puxinxiaolin.pojo.MappedStatement;

import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    /**
     * 查询所有
     *
     * @param statementId 根据这个可以在前面传递下来的 Configuration 中获取 MappedStatement
     * @param param
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> selectList(String statementId, Object param) throws Exception {
        // 具体操作委派给 executor，执行底层 jdbc
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return executor.query(configuration, mappedStatement, param);
    }

    /**
     * 查询单个
     *
     * @param statementId 根据这个可以在前面传递下来的 Configuration 中获取 MappedStatement
     * @param param
     * @param <T>
     * @return
     */
    @Override
    public <T> T selectOne(String statementId, Object param) throws Exception {
        List<Object> list = this.selectList(statementId, param);

        if (list.size() > 1) {
            throw new RuntimeException("返回结果过多");
        } else if (list.isEmpty()) {
            return null;
        }
        return (T) list.get(0);
    }

    /**
     * 关闭资源
     */
    @Override
    public void close() {
        executor.close();
    }
}
