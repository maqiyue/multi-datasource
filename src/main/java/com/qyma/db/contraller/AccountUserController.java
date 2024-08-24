package com.qyma.db.contraller;

import com.qyma.db.service.TestService1;
import com.qyma.db.manager.MultiRouterManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
public class AccountUserController {



    @Resource
    private TestService1 testService1;



    @GetMapping
    public String createAccount(String name,String id) {
        MultiRouterManager.tenantContext.set(id);
        return testService1.createAccount(name);
    }

    @GetMapping("/user")
    public String createUser(String name,String id) {
        MultiRouterManager.tenantContext.set(id);
        return testService1.createUser(name);
    }

    @GetMapping("/test1")
    public String test1(String name,String id) {
        MultiRouterManager.tenantContext.set(id);
        return testService1.test1(name);
    }


}
