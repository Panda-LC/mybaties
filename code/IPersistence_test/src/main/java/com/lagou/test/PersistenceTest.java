package com.lagou.test;

import com.lagou.dao.UserDao;
import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class PersistenceTest {

    private UserDao userDao;

    @Before
    public void initSqlSession() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sessionFactory.openSqlSession();
        userDao = sqlSession.getMapper(UserDao.class);
    }

    @Test
    public void test_selectOne_proxy() throws Exception {
        User user = new User();
        user.setId("2");
        user.setUsername("bbb");
        System.out.println(userDao.selectOne(user));
    }

    @Test
    public void test_selectList_proxy() throws Exception {
        List<User> userResult = userDao.selectList();
        for (User user : userResult) {
            System.out.println(user);
        }
    }

    @Test
    public void test_deleteById_proxy() throws Exception {
        System.out.println(userDao.deleteById("1"));
    }

    @Test
    public void test_insert_proxy() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(System.currentTimeMillis()+"");
        System.out.println(userDao.insert(user));
    }

    @Test
    public void test_update_proxy() throws Exception {
        User user = new User();
        user.setId("1");
        user.setUsername(System.currentTimeMillis()+"");
        System.out.println(userDao.update(user));
    }

}