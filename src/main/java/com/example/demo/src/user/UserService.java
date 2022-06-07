package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.PostMoimReq;
import com.example.demo.src.user.model.PostMoimRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }


    public PostMoimRes createMoim(PostMoimReq postMoimreq) throws BaseException {
        try {
            int moimIdx = userDao.createMoim(postMoimreq);
            return new PostMoimRes(moimIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
