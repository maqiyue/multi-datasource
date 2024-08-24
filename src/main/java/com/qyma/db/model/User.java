package com.qyma.db.model;


import lombok.Data;

@Data
public class User {
    private int id;
    private String account;
    private int departmentId;

    // Getters and Setters
}
