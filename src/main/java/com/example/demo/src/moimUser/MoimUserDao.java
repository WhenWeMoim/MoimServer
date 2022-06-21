package com.example.demo.src.moimUser;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MoimUserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String selectcheckPasswd(int moimIdx) {
        String selectcheckPasswdQuery = "select passwd from Moim where moimIdx = ?";
        return jdbcTemplate.queryForObject(selectcheckPasswdQuery, String.class, moimIdx);
    }

    public void addUser(int moimIdx, int userIdx) {
    // Step 3 MoimUser 테이블 데이터 생성
    String addMoimUserQuery = "insert into MoimUser (moimIdx, userIdx, schedule) VALUES (?,?,?)";
        this.jdbcTemplate.update(addMoimUserQuery, moimIdx, userIdx, null);

        return;
    }
}