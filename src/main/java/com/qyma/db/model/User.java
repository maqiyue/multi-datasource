package com.qyma.db.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user") // 数据库表名
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account",  columnDefinition = "varchar(255) COMMENT '账户名'") // 账户名
    private String account;

    @Column(name = "department_id", columnDefinition = "int COMMENT '部门ID'") // 部门ID
    private int departmentId;
}
