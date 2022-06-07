package com.example.demo.src.moim;


import com.example.demo.config.BaseException;
import com.example.demo.src.moim.model.*;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class MoimProvider {

    private final MoimDao moimDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MoimProvider(MoimDao moimDao, JwtService jwtService) {
        this.moimDao = moimDao;
        this.jwtService = jwtService;
    }

    /**
     * dev server 연결 테스트
     */
    public GetUserRes getUsersByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUsersRes = moimDao.getUsersByIdx(userIdx);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserMoims getUserMoimsByUserIdx(int userIdx) throws BaseException {
        try{
            List<MoimBriefInfo> moimBriefInfos = moimDao.getUserMoimsByUserIdx(userIdx);
            return new GetUserMoims(moimBriefInfos);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Moim getMoim(int moimIdx) throws BaseException {
        try{
            MoimInfo moimInfo = moimDao.getMoimInfo(moimIdx);
            //step 1. moimIdx로 moimDateIdxList와 moimUserIdxList를 만든다.
            List<Integer> moimDateIdxList = moimDao.getMoimDateIdxList(moimIdx);
            List<Integer> moimUserIdxList = moimDao.getMoimUserIdxList(moimIdx);

            //step 2.
            List<UserSchedule> userScheduleList = new ArrayList<>();
            int userNum = moimUserIdxList.size();
            for(int i = 0; i < userNum; i++) {
                int userIdx = moimUserIdxList.get(i);
                List<MoimPersonalSchedule> moimPersonalSchedules = moimDao.getMoimUserSchedule(userIdx, moimDateIdxList);
                userScheduleList.add(new UserSchedule(userIdx, moimPersonalSchedules));
            }

            return new Moim(moimInfo, userScheduleList);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
