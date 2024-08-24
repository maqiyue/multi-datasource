package com.qyma.db.mapper;




import com.qyma.db.annotation.Tenant;
import com.qyma.db.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Tenant
@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user (account, department_id) VALUES (#{account}, #{departmentId})")
    void insertUser(User user);

    @Select("SELECT * FROM user")
    List<User> selectAllUsers();
}

