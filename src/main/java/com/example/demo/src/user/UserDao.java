package com.example.demo.src.user;


import com.example.demo.src.user.model.GetUserLoginReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int getUserLogin(GetUserLoginReq getUserLoginReq) {
        String userLoginQuery = "insert into User (userName, ps) VALUES (?,?)";
        this.jdbcTemplate.update(userLoginQuery, getUserLoginReq.getUserName(), getUserLoginReq.getPs());

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
}
