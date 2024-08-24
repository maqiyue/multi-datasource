package com.qyma.db.service;

import com.qyma.db.mapper.AccountMapper;
import com.qyma.db.mapper.UserMapper;
import com.qyma.db.model.Account;
import com.qyma.db.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class TestService2 {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.NOT_SUPPORTED)
    public String test1A(String name) {
        User user = new User();
        user.setAccount(name);
        user.setDepartmentId(1);
        userMapper.insertUser(user);
        userMapper.insertUser(user);
        return "Account and User created successfully!";
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public String test1B(String name) {
        User user = new User();
        user.setAccount(name);
        user.setDepartmentId(1);
        userMapper.insertUser(user);
        return "Account and User created successfully!";
    }

    public String test1C(String name) {
        User user = new User();
        user.setAccount(name);
        user.setDepartmentId(1);
        userMapper.insertUser(user);
        return "Account and User created successfully!";
    }

}
