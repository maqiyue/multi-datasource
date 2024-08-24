package com.qyma.db.aspect;


import com.qyma.db.annotation.Tenant;
import com.qyma.db.manager.MultiRouterManager;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class TenantDataSourceAspect {

    @Bean
    public Advisor advisor() {
        Pointcut pointcut = new AnnotationMatchingPointcut(Tenant.class, true);
        Advice advice = new MethodAroundAdvice();
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    private static class MethodAroundAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

        @Override
        public void before(Method method, Object[] args, Object target) throws Throwable {
            MultiRouterManager.dataSourceContext.set("tenant");
        }

        @Override
        public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
            MultiRouterManager.dataSourceContext.remove();
        }
    }
}
