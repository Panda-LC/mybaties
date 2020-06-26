package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession{

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
           throw new RuntimeException("查询结果为空或者返回结果过多");
        }
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        return update(statementId, params);
    }

    @Override
    public int insert(String statementId, Object... params) throws Exception {
        return update(statementId, params);
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.update(configuration, mappedStatement, params);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("代理模式开启...");

                //规范接口名字等于xml的id名，变相获取到statementId
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;
                String nodeType = getNodeType(statementId);
                if (nodeType.equals("select")) {
                    //获取返回对象的类型
                    Type genericReturnType = method.getGenericReturnType();
                    if (genericReturnType instanceof ParameterizedType) {
                        return selectList(statementId, args);
                    } else {
                        return selectOne(statementId, args);
                    }
                } else if (nodeType.equals("insert")) {
                    return insert(statementId, args);
                } else if (nodeType.equals("delete")) {
                    return delete(statementId, args);
                } else {
                    return update(statementId, args);
                }
            }
        });
        return (T) proxyInstance;
    }

    //获取该sql对应的节点类型
    private String getNodeType(String statementId) {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        if (mappedStatement != null && null != mappedStatement.getNodeType()){
            if (!"select".equals(mappedStatement.getNodeType()) &&
                !"insert".equals(mappedStatement.getNodeType()) &&
                !"delete".equals(mappedStatement.getNodeType()) &&
                !"update".equals(mappedStatement.getNodeType())) {
                throw new RuntimeException(statementId+"为不支持的节点类型");
            } else {
                return mappedStatement.getNodeType();
            }
        } else {
            throw new RuntimeException(statementId+"为不支持的节点类型");
        }
    }

}