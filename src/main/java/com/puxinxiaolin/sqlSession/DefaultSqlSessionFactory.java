package com.puxinxiaolin.sqlSession;

import com.puxinxiaolin.executor.Executor;
import com.puxinxiaolin.executor.SimpleExecutor;
import com.puxinxiaolin.pojo.Configuration;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 创建 SqlSession 对象和执行器对象
     *
     * @return
     */
    @Override
    public SqlSession openSession() {
        // 1. 创建执行器对象
        Executor executor = new SimpleExecutor();

        // 2. 创建 SqlSession 对象
        return new DefaultSqlSession(configuration, executor);
    }
}
