package com.puxinxiaolin.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询所有
     *
     * @param statementId 根据这个可以在前面传递下来的 Configuration 中获取 MappedStatement
     * @param param
     * @param <E>
     * @return
     */
    <E> List<E> selectList(String statementId, Object param) throws Exception;

    /**
     * 查询单个
     *
     * @param statementId 根据这个可以在前面传递下来的 Configuration 中获取 MappedStatement
     * @param param
     * @param <T>
     * @return
     */
    <T> T selectOne(String statementId, Object param) throws Exception;

    /**
     * 清除资源
     */
    void close();

//    Boolean update();
//
//    Boolean insert();
//
//    Boolean delete();

}
