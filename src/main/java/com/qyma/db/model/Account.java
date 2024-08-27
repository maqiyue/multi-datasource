package com.qyma.db.model;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import lombok.Data;
@Data
@Entity
@Table(name = "account") // 数据库表名
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account", nullable = false, columnDefinition = "varchar(255) COMMENT '账户名'") // 账户名
    private String account;

    @Column(name = "password", nullable = false, columnDefinition = "varchar(255) COMMENT '密码'") // 密码
    private String password;
}

