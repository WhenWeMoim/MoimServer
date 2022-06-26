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

    public PostMoimRes createMoim(PostMoimReq postMoimreq) throws BaseException {
        try {
            int moimIdx = moimDao.createMoim(postMoimreq);
            return new PostMoimRes(moimIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int modifyPersonalSchedule(int moimIdx, int userIdx, String schedule) throws BaseException {
        try {
            return moimDao.updatePersonalSchedule(moimIdx, userIdx, schedule);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
