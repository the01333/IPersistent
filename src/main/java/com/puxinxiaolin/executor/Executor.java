package com.puxinxiaolin.executor;

import com.puxinxiaolin.pojo.Configuration;
import com.puxinxiaolin.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object param) throws Exception;

    void close();

}
