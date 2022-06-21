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
    String addMoimUserQuery = "insert into MoimUser (moimIdx, userIdx) VALUES (?,?)";
        this.jdbcTemplate.update(addMoimUserQuery, moimIdx, userIdx);

    // Step 4 PersonalSchedule 테이블 데이터 생성
    String selectMoimDateIdxQuery = "select moimDateIdx from MoimDate where moimIdx = ?";
    List<Integer> moimdates = this.jdbcTemplate.query(selectMoimDateIdxQuery,
            (rs, rowNum) -> (rs.getInt("moimDateIdx")),
            moimIdx);

    String createPersonalScheduleQuery = "insert into PersonalSchedule (moimDateIdx, moimIdx, userIdx, schedule)\n" +
            " VALUES (?,?,?,?);";
        if (moimdates.size() > 20)
                return;
        else {
        for (int i = 0; i < moimdates.size(); i++) {
            this.jdbcTemplate.update(createPersonalScheduleQuery,
                    moimdates.get(i), moimIdx, userIdx, "112" );
        }
    }

        return;
    }
}