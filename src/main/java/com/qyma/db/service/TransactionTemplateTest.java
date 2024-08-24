//package com.qyma.db.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import javax.annotation.Resource;
//
//@Service
//public class TransactionTemplateTest {
//
//    @Resource
//    private TransactionTemplate transactionTemplate1;
//
//    @Resource
//    private TransactionTemplate transactionTemplate2;
//
//    /**
//     * 编程式事务例子
//     */
//    public void performTransactionalOperation() {
//        try {
//            transactionTemplate1.execute(status -> {
//                performOperation1();
//                return null;
//            });
//
//            transactionTemplate2.execute(status -> {
//                performOperation2();
//                return null;
//            });
//
//        } catch (Exception e) {
//            // 如果有需要，可以在这里处理异常
//            throw e;
//        }
//    }
//
//    private void performOperation1() {
//        // 执行操作1
//    }
//
//    private void performOperation2() {
//        // 执行操作2
//    }
//}
//
