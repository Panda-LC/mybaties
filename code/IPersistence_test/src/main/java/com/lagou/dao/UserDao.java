package com.lagou.dao;

import com.lagou.pojo.User;

import java.util.List;

public interface UserDao {

    public List<User> selectList();

    public User selectOne(User user);

    public int deleteById(String id);

    public int insert(User user);

    public int update(User user);

}
