package com.qyma.db.service;

import com.qyma.db.mapper.AccountMapper;
import com.qyma.db.mapper.UserMapper;
import com.qyma.db.model.Account;
import com.qyma.db.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

@Service
public class TestService1 {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TestService2 testService2;


    /**
     * common的事务
     */
    @Transactional(rollbackFor = Exception.class)
    public String createAccount(String name) {
        Account account = new Account();
        account.setAccount(name);
        account.setPassword(name);
        accountMapper.insertAccount(account);
        accountMapper.selectAllAccounts();
        userMapper.selectAllUsers();

        User user = new User();
        user.setAccount(name);
        user.setDepartmentId(1);
        userMapper.insertUser(user);
        userMapper.insertUser(user);

        createUser(name);
        testService2.test1C(name);
        int a= 1/0;
        return "Account and User created successfully!";
    }

    /**
     * tenant的事务
     */

    public String createUser(String name) {
        User user = new User();
        user.setAccount(name);
        user.setDepartmentId(1);
        userMapper.insertUser(user);
        userMapper.insertUser(user);
        return "Account and User created successfully!";
    }


    /**
     * 综合测试  s
     */
    @Transactional
    public String test1(String name) {


        /*
        处理tenant
        如果方法写在同一个service中 tenant的事务管理器将不生效（直接继承上面的common的事务）
        写在其他的service中，testService2.test1A中使用了NOT_SUPPORTED 不加入事务， 不加入事务的效果就是 如果test1A中的多个操作发生了异常，test1A不会回滚,但是由于test1中有事务 test1会回滚
        写在其他的service中，testService2.test1B中使用了REQUIRES_NEW 不加入事务， 不加入事务的效果就是 如果test1B中的多个操作发生了异常，test1B会回滚, test1会回滚 ,但是如果执行了test1B之后，test1发生异常，test1B不会回滚
        test1B不加事务 会直接报错找不到表 因为会继承test1的事务
         */

//        testService2.test1A(name);
        testService2.test1B(name);


        Account account = new Account();
        account.setAccount(name);
        account.setPassword(name);
        accountMapper.insertAccount(account);
        accountMapper.insertAccount(account);
        return "Account and User created successfully!";
    }



    public String test2(String name) {


//        testService2.test1A(name);
        testService2.test1B(name);


        Account account = new Account();
        account.setAccount(name);
        account.setPassword(name);
        accountMapper.insertAccount(account);
        accountMapper.insertAccount(account);
        return "Account and User created successfully!";
    }


















    public String createAccount2(String name) {
        Account account = new Account();
        account.setAccount(name);
        account.setPassword(name);
        accountMapper.insertAccount(account);

        return "Account and User created successfully!";
    }



}
