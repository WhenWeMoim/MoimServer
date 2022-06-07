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
        List<String> dates = postMoimReq.getDates();
        if( dates.size() > 20 )
            return lastMoimIdx;
        else {
            // date의 형태가 "2022-05-04" 형태인지 확인하는 validation 필요
            for (int i = 0; i < dates.size(); i++) {
                this.jdbcTemplate.update(createMoimDate, lastMoimIdx, dates.get(i));
            }
        }
        // Step 3 MoimUser 테이블 데이터 생성
        String createMoimUserQuery = "insert into MoimUser (moimIdx, userIdx) VALUES (?,?)";
        this.jdbcTemplate.update(createMoimUserQuery, lastMoimIdx, postMoimReq.getUserIdx());

        // Step 4 PersonalSchedule 테이블 데이터 생성
        String selectMoimDateIdxQuery = "select moimDateIdx from MoimDate where moimIdx = ?";
        List<Integer> moimdates = this.jdbcTemplate.query(selectMoimDateIdxQuery,
                (rs, rowNum) -> (rs.getInt("moimDateIdx")),
                lastMoimIdx);

        String createPersonalScheduleQuery = "insert into PersonalSchedule (moimDateIdx, moimIdx, userIdx, schedule)\n" +
                " VALUES (?,?,?,?);";
        if (moimdates.size() > 20)
            return lastMoimIdx;
        else {
            for (int i = 0; i < moimdates.size(); i++) {
                this.jdbcTemplate.update(createPersonalScheduleQuery,
                        moimdates.get(i), lastMoimIdx, postMoimReq.getUserIdx(), "112" );
            }
        }
        return lastMoimIdx;
    }

    public TestAddUser testAddDummyUser(TestAddUser testAddUser) {
        // Step 3 MoimUser 테이블 데이터 생성
        String testCreateMoimUserQuery = "insert into MoimUser (moimIdx, userIdx) VALUES (?,?)";
        this.jdbcTemplate.update(testCreateMoimUserQuery, testAddUser.getMoimIdx(), testAddUser.getUserIdx());

        // Step 4 PersonalSchedule 테이블 데이터 생성
        String testSelectMoimDateIdxQuery = "select moimDateIdx from MoimDate where moimIdx = ?";
        List<Integer> moimdates = this.jdbcTemplate.query(testSelectMoimDateIdxQuery,
                (rs, rowNum) -> (rs.getInt("moimDateIdx")),
                testAddUser.getMoimIdx());

        String testCreatePersonalScheduleQuery = "insert into PersonalSchedule (moimDateIdx, moimIdx, userIdx, schedule)\n" +
                " VALUES (?,?,?,?);";
        if (moimdates.size() > 20)
            return testAddUser;
        else {
            for (int i = 0; i < moimdates.size(); i++) {
                this.jdbcTemplate.update(testCreatePersonalScheduleQuery,
                        moimdates.get(i), testAddUser.getMoimIdx(), testAddUser.getUserIdx(), "112" );
            }
        }

        return testAddUser;
    }

    public MoimInfo getMoimInfo(int MoimIdx) {
        String getMoimInfoQuery = "select moimIdx, moimTitle, moimDescription,\n" +
                "       masterUserIdx, startTime, endTime\n" +
                "from Moim\n" +
                "where moimIdx = ?;";
        int getMoimInfoParam = MoimIdx;
        return this.jdbcTemplate.queryForObject(getMoimInfoQuery,
                (rs, rowNum) -> new MoimInfo(
                        rs.getInt("moimIdx"),
                        rs.getString("moimTitle"),
                        rs.getString("moimDescription"),
                        rs.getInt("masterUserIdx"),
                        rs.getString("startTime"),
                        rs.getString("endTime")
                ),
                getMoimInfoParam);
    }

    public List<Integer> getMoimUserIdxList(int moimIdx) {
        String getMoimUserIdxQuery = "select userIdx from MoimUser where moimIdx = ?";
        int getMoimUserIdxParam = moimIdx;
        List<Integer> moimUserIdxList = this.jdbcTemplate.query(getMoimUserIdxQuery,
                (rs, rowNum) -> (rs.getInt("userIdx")),
                getMoimUserIdxParam);
        return moimUserIdxList;
    }

    public List<Integer> getMoimDateIdxList(int moimIdx) {
        String getMoimDateIdxQuery = "select moimDateIdx from MoimDate where moimIdx = ?";
        int getMoimDateIdxParam = moimIdx;
        List<Integer> moimDateIdxList = this.jdbcTemplate.query(getMoimDateIdxQuery,
                (rs, rowNum) -> (rs.getInt("moimDateIdx")),
                getMoimDateIdxParam);
        return moimDateIdxList;
    }


    public List<MoimPersonalSchedule> getMoimUserSchedule(int userIdx, List<Integer> moimDateIdxList) {
        String getDateByMoimDateIdxQuery ="select date\n" +
                "    from MoimDate\n" +
                "    where moimDateIdx = ?";
        String getScheduleQuery = "select schedule\n" +
                "    from PersonalSchedule\n" +
                "    where moimDateIdx = ? and userIdx = ?";

        List<MoimPersonalSchedule> moimPersonalScheduleList = new ArrayList<>();
        int moimDateNum = moimDateIdxList.size();
        int moimDateIdx=0;
        String date = null;
        String schedule = null;
        for(int i = 0; i < moimDateNum; i++) {
            moimDateIdx = moimDateIdxList.get(i);
            schedule = jdbcTemplate.queryForObject(getScheduleQuery, String.class, moimDateIdx, userIdx);
            moimPersonalScheduleList.add(new MoimPersonalSchedule(schedule));
        }
        return moimPersonalScheduleList;
    }

    public int selectMoimDateIdxByDatas(int moimIdx, MoimPersonalSchedule moimPersonalSchedule) {
    String getMoimDateIdxByDatasQuery = "select moimDateIdx\n" +
            "    from MoimDate\n" +
            "    where moimIdx = ? and date = ?";
    Object[] getMoimUserIdxParams = new Object[] { moimIdx, moimPersonalSchedule.getDates() };
    int moimDateIdx = this.jdbcTemplate.queryForObject(getMoimDateIdxByDatasQuery, int.class,
            getMoimUserIdxParams);
        return moimDateIdx;
    }

    public int updatePersonalSchedule(int moimDateIdx, int userIdx, MoimPersonalSchedule moimPersonalSchedule) {
        String updatePersonalScheduleQuery = "update PersonalSchedule set schedule = ?\n" +
                "where moimDateIdx = ? and userIdx = ?";
        Object[] updatePersonalScheduleParams = new Object[] { moimPersonalSchedule.getSchedules(), moimDateIdx, userIdx};
        return this.jdbcTemplate.update(updatePersonalScheduleQuery, updatePersonalScheduleParams);

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
