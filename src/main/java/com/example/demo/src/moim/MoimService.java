package com.example.demo.src.moim;


import com.example.demo.config.BaseException;
import com.example.demo.src.moim.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

// Service Create, Update, Delete 의 로직 처리
@Service
public class MoimService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MoimDao moimDao;
    private final MoimProvider moimProvider;
    private final JwtService jwtService;


    @Autowired
    public MoimService(MoimDao moimDao, MoimProvider moimProvider, JwtService jwtService) {
        this.moimDao = moimDao;
        this.moimProvider = moimProvider;
        this.jwtService = jwtService;

    }

    public TestAddUser testAddDummyUser(TestAddUser testAddUser) throws BaseException {
        try {
            TestAddUser response = moimDao.testAddDummyUser(testAddUser);
            return response;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostMoimRes createMoim(PostMoimReq postMoimreq) throws BaseException {
        try {
            int moimIdx = moimDao.createMoim(postMoimreq);
            return new PostMoimRes(moimIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPersonalSchedule(int moimIdx, int userIdx, List<MoimPersonalSchedule> moimPersonalScheduleList) throws BaseException {
        try {
            int count = moimPersonalScheduleList.size();
            int moimDateIdx = 0;
            MoimPersonalSchedule moimPersonalSchedule;
            for(int i=0; i < count; i++) {
                moimPersonalSchedule = moimPersonalScheduleList.get(i);
                // step 1. MoimDate 테이블: moimIdx와 date로 moimDateIdx를 특정한다.
                // 인자로 moimIdx와 MoimPersonalSchedule 한 개를 준다.
                moimDateIdx = moimDao.selectMoimDateIdxByDatas(moimIdx, moimPersonalSchedule);
                // step 2. PersonalSchedule 테이블: moimDateIdx와 userIdx로 특정하여 schedule 변경
                // 인자로 MoimPersonalSchedule 한 개와 userIdx, moimDateIdx 한 개를 준다.
                moimDao.updatePersonalSchedule(moimDateIdx, userIdx, moimPersonalSchedule);
                // step 3. 반복
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String updateMoimPassword(int moimIdx) throws BaseException {
        try {
            return moimDao.updateMoimPassword(moimIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
