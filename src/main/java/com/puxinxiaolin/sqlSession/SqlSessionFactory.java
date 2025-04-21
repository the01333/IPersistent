package com.puxinxiaolin.sqlSession;

public interface SqlSessionFactory {

    /**
     * 创建 SqlSession 对象和执行器对象
     *
     * @return
     */
    SqlSession openSession();

}
