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

    public int createMoim(PostMoimReq postMoimReq) {
        // Step 1 Moim 테이블 데이터 생성
        String createMoimQuery = "insert into Moim (moimTitle, moimDescription, masterUserIdx, startTime, endTime)\n" +
                " VALUES (?,?,?,?,?);";
        this.jdbcTemplate.update(createMoimQuery, postMoimReq.getMoimTitle(), postMoimReq.getMoimDescription(),
                postMoimReq.getUserIdx(), postMoimReq.getStartTime(), postMoimReq.getEndTime());

        String lastInsertIdQuery = "select last_insert_id()";
        int lastMoimIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

        // Step 2 MoimDate 테이블 데이터 생성
        String createMoimDate = "insert into MoimDate (moimIdx, date) VALUES (?,?)";
        List<Integer> dates = postMoimReq.getDates();
        if( dates.size() > 20 )
            return lastMoimIdx;
        else {
            // date의 형태가 "2022-05-04" 형태인지 확인하는 validation 필요
            for (int i = 0; i < dates.size(); i++) {
                this.jdbcTemplate.update(createMoimDate, lastMoimIdx, dates.get(i));
            }
        }
        // Step 3 MoimUser 테이블 데이터 생성
        String createMoimUserQuery = "insert into MoimUser (moimIdx, userIdx, schedule) VALUES (?,?,?)";
        this.jdbcTemplate.update(createMoimUserQuery, lastMoimIdx, postMoimReq.getUserIdx(),null);

        return lastMoimIdx;
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

    public GetMoimInfoRes getMoimInfo(int moimIdx) {

        // moimInfo
        String getMoimInfoQuery = "select moimIdx, moimTitle, moimDescription,\n" +
                "       masterUserIdx, startTime, endTime\n" +
                "from Moim\n" +
                "where moimIdx = ?;";
        int getMoimInfoParam = moimIdx;
        MoimInfo moimInfo = this.jdbcTemplate.queryForObject(getMoimInfoQuery,
                (rs, rowNum) -> new MoimInfo(
                        rs.getInt("moimIdx"),
                        rs.getString("moimTitle"),
                        rs.getString("moimDescription"),
                        rs.getInt("masterUserIdx"),
                        rs.getString("startTime"),
                        rs.getString("endTime")
                ),
                getMoimInfoParam);
        // userIdx List
        String getMoimUserIdxQuery = "select userIdx from MoimUser where moimIdx = ?";
        int getMoimUserIdxParam = moimIdx;
        List<Integer> moimUserIdxList = this.jdbcTemplate.query(getMoimUserIdxQuery,
                (rs, rowNum) -> (rs.getInt("userIdx")),
                getMoimUserIdxParam);
        int userNum = moimUserIdxList.size();
        // schedule List
        String getMoimPersonalScheduleQuery = "select schedule from MoimUser where moimIdx = ? and userIdx = ?";
        List<MoimPersonalSchedule> moimUserSchedules = new ArrayList<MoimPersonalSchedule>();
        for(int i =0; i < userNum; i++) {
            String schedule = this.jdbcTemplate.queryForObject(getMoimPersonalScheduleQuery,
                    String.class, moimIdx, moimUserIdxList.get(i));
            MoimPersonalSchedule moimPersonalSchedule = new MoimPersonalSchedule(moimUserIdxList.get(i),schedule);
            moimUserSchedules.add(moimPersonalSchedule);
        }
        return new GetMoimInfoRes(moimInfo, moimUserSchedules);
    }

    public int updatePersonalSchedule(PatchMoimUserScheduleReq patchMoimUserScheduleReq) {
        String updatePersonalScheduleQuery = "update MoimUser set schedule = ?\n" +
                "where moimIdx = ? and userIdx = ?";
        Object[] updatePersonalScheduleParams = new Object[] {
                patchMoimUserScheduleReq.getSchedule(),
                patchMoimUserScheduleReq.getMoimIdx(),
                patchMoimUserScheduleReq.getUserIdx()};
        return this.jdbcTemplate.update(updatePersonalScheduleQuery, updatePersonalScheduleParams);
    }

}
