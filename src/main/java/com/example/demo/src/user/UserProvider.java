package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    /**
     * dev server 연결 테스트
     */
    public GetUserRes getUsersByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUsersRes = userDao.getUsersByIdx(userIdx);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserMoimTitles getUserMoimTitlesByUserIdx(int UserIdx) throws BaseException {
        try{
            List<String> moimTitles = userDao.getUserMoimTitlesByUserIdx(UserIdx);
            return new GetUserMoimTitles(moimTitles);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Moim getMoim(int MoimIdx) throws BaseException {
        try{
            MoimInfo moimInfo = userDao.getMoimInfo(MoimIdx);
            List<UserSchedule> moimUserScheduleList = userDao.getMoimUserSchedule(MoimIdx);
            return new Moim(moimInfo, moimUserScheduleList);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
