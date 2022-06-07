package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Repository
public class UserDao {

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

    public List<String> getUserMoimTitlesByUserIdx(int userIdx){
        String getUsersByUserNameQuery = "select moimTitle\n" +
                "from Moim u\n" +
                "    join (select moimIdx\n" +
                "          from MoimUser\n" +
                "          where userIdx = ?) p on p.moimIdx = u.moimIdx;";
        int getUsersByUserIdxParams = userIdx;
        return this.jdbcTemplate.query(getUsersByUserNameQuery,
                (rs, rowNum) -> new String(
                        rs.getString("moimTitle")
                        ), getUsersByUserIdxParams);
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

    public List<UserSchedule> getMoimUserSchedule(int MoimIdx) {
        // Step 1. 
        // 1-1 MoimIdx를 이용하여 moimDateIdx의 List를 만든다.
        // 1-2 "2022-05-03" 형태로 저장하는 List를 만든다.
        // 1-3moim에 속한 userIdx의 List를 만든다.
        // 1-1
        String getMoimDateIdxQuery = "select moimDateIdx from MoimDate where moimIdx = ?";
        int getMoimDateIdxParam = MoimIdx;
        List<Integer> moimDateIdxList = this.jdbcTemplate.query(getMoimDateIdxQuery,
                (rs, rowNum) -> (rs.getInt("moimDateIdx")),
                getMoimDateIdxParam);
        // 1-2
        String getMoimDateQuery = "select date\n" +
                "from MoimDate\n" +
                "where moimIdx = ?";
        int getMoimDateParam = MoimIdx;
        List<String> moimdateList = this.jdbcTemplate.query(getMoimDateQuery,
                (rs, rowNum) -> (rs.getString("date")),
                getMoimDateParam);
        // 1-3
        String getMoimUserIdxQuery = "select userIdx from MoimUser where moimIdx = ?";
        int getMoimUserIdxParam = MoimIdx;
        List<Integer> moimUserIdxList = this.jdbcTemplate.query(getMoimUserIdxQuery,
                (rs, rowNum) -> (rs.getInt("userIdx")),
                getMoimUserIdxParam);
        // Step 2
        // moimdateIdxList와 userIdx를 이용해서 schedule의 List를 만든다.
        // userIdxList, moimdateList, userScheduleList를 이용해서 UserSchedule의 List를 만든다.
        String getMoimScheduleQuery = "select schedule \n" +
                "from PersonalSchedule \n" +
                "where moimdateIdx = ? and userIdx = ?";

        List<UserSchedule> userScheduleList = new ArrayList<>();
        List<String> personalUserScheduleList = null;
        int moimUserIdx = 0;
        int moimDateIdx = 0;
        int dateSize = moimdateList.size();
        if(moimUserIdxList.size() > 100)
            return new ArrayList<UserSchedule>();
        else {
            for (int i = 0; i < dateSize; i++) {
                moimUserIdx = moimUserIdxList.get(i);
                for (int j = 0; j < dateSize; j++) {
                    moimDateIdx = moimDateIdxList.get( i*dateSize + j );
                    personalUserScheduleList = this.jdbcTemplate.query(getMoimScheduleQuery,
                            (rs, rowNum) -> (rs.getString("schedule")),
                            moimDateIdx, moimUserIdx);
                }
                userScheduleList.add(new UserSchedule(moimUserIdx, moimdateList, personalUserScheduleList));
            }
        }
            // Step 2-2. step1, step2로 찾아온 정보로 UserSchedule 모델의 List를 만든다.
        // Step 3. 만든 모델의 List를 return한다.
        return userScheduleList;
    }
}
