package com.example.demo.src.moimUser;


import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.JOIN_PASSWORD_ERROR;

// Service Create, Update, Delete 의 로직 처리
@Service
public class MoimUserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MoimUserDao moimUserDao;
    private final MoimUserProvider moimUserProvider;
    private final JwtService jwtService;


    @Autowired
    public MoimUserService(MoimUserDao moimUserDao, MoimUserProvider moimUserProvider, JwtService jwtService) {
        this.moimUserDao = moimUserDao;
        this.moimUserProvider = moimUserProvider;
        this.jwtService = jwtService;

    }

    public String joinMoim(int moimIdx, int userIdx, String passwd) throws BaseException{
        //try{
            String checkPasswd = moimUserDao.selectcheckPasswd(moimIdx);
            String message;
            System.out.println(checkPasswd.equals(passwd));
            if(checkPasswd.equals(passwd)) {
                moimUserDao.addUser(moimIdx, userIdx);
                message = "모임 참가에 성공했습니다.";
                return message;
            }
            else {
                System.out.println(checkPasswd.equals(passwd));
                message = "패스워드가 틀렸습니다.";
                throw new BaseException(JOIN_PASSWORD_ERROR);
            }
        //}
        //catch (Exception exception) {
        //    throw new BaseException(DATABASE_ERROR);
        //}
    }
}