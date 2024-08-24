package com.qyma.db.mapper;

import com.qyma.db.model.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AccountMapper {

    @Insert("INSERT INTO account (account, password) VALUES (#{account}, #{password})")
    void insertAccount(Account account);

    @Select("SELECT * FROM account")
    List<Account> selectAllAccounts();
}
