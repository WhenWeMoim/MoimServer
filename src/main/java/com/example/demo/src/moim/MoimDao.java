package com.example.demo.src.moim;


import com.example.demo.src.moim.model.*;
import com.example.demo.src.user.model.GetUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Repository
public class MoimDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * dev 서버 연결 테스트
     */
    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select userIdx,userName from User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName")),
                getUsersByIdxParams);
    }

    public List<MoimBriefInfo> getUserMoimsByUserIdx(int userIdx){
        String getUsersByUserNameQuery = "select u.moimIdx, moimTitle, moimDescription\n" +
                "from Moim u\n" +
                "join (select moimIdx\n" +
                "from MoimUser\n" +
                "where userIdx = ?) p on p.moimIdx = u.moimIdx;";
        int getUsersByUserIdxParams = userIdx;
        List<MoimBriefInfo> moimBriefInfos = this.jdbcTemplate.query(getUsersByUserNameQuery,
                (rs, rowNum) -> new MoimBriefInfo(
                        rs.getInt("u.moimIdx"),
                        rs.getString("moimTitle"),
                        rs.getString("moimDescription")
                ), getUsersByUserIdxParams);
        return moimBriefInfos;
    }

    public String updateMoimPassword(int moimIdx) {
        String updateMoimPasswordQuery = "update Moim set passwd = ? where moimIdx = ?";

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        String newPassword ="";
        for(int i=0; i<4; i++) {
            newPassword += Integer.toString(((int)(random.nextDouble() * 10)) % 10);
        }

        this.jdbcTemplate.update(updateMoimPasswordQuery, newPassword, moimIdx);
        return newPassword;
    }

}
