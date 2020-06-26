package com.lagou.sqlSession;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.util.GenericTokenParser;
import com.lagou.util.ParameterMapping;
import com.lagou.util.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor{

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //得到预编译
        PreparedStatement preparedStatement = getPreparedStatement(configuration, mappedStatement, params);

        //执行sql获得返回结果
        ResultSet resultSet = preparedStatement.executeQuery();

        //将返回结果封装到返回的实体类中
        String resultType = mappedStatement.getResultType();
        Class<?> resulttypeClass = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<Object>();
        while (resultSet.next()) {
            Object o = resulttypeClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(columnName);

                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resulttypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }

        return (List<E>) objects;
    }

    @Override
    public int insert(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //得到预编译
        PreparedStatement preparedStatement = getPreparedStatement(configuration, mappedStatement, params);

        //执行sql获得返回结果
        int deleteResult = preparedStatement.executeUpdate();
        return deleteResult;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //得到预编译
        PreparedStatement preparedStatement = getPreparedStatement(configuration, mappedStatement, params);

        //执行sql获得返回结果
        int deleteResult = preparedStatement.executeUpdate();
        return deleteResult;
    }

    @Override
    public int delete(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //得到预编译
        PreparedStatement preparedStatement = getPreparedStatement(configuration, mappedStatement, params);

        //执行sql获得返回结果
        int deleteResult = preparedStatement.executeUpdate();
        return deleteResult;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
        return null;
    }

    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String parseSql = genericTokenParser.parse(sql);
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }

    private PreparedStatement getPreparedStatement(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //获取数据库连接
        Connection connection = configuration.getDataSource().getConnection();
        connection.setAutoCommit(true);//设置自动提交

        //解析sql
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        //得到预编译
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //获得参数，填写到动态sql中
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        if (parameterMappingList.size() == 1) {
            preparedStatement.setObject(1, params[0]);
        } else {
            String parameterType = mappedStatement.getParamterType();
            Class<?> parametertypeClass = getClassType(parameterType);
            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();

                Field declaredField = parametertypeClass.getDeclaredField(content);
                declaredField.setAccessible(true);
                Object o = declaredField.get(params[0]);
                preparedStatement.setObject(i+1, o);
            }
        }
        return preparedStatement;
    }

}