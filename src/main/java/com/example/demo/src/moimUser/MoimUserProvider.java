package com.example.demo.src.moimUser;


import com.example.demo.config.BaseException;
import com.example.demo.src.moim.model.Moim;
import com.example.demo.src.moim.model.MoimInfo;
import com.example.demo.src.moim.model.UserSchedule;
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
public class MoimUserProvider {

    private final MoimUserDao moimUserDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MoimUserProvider(MoimUserDao moimUserDao, JwtService jwtService) {
        this.moimUserDao = moimUserDao;
        this.jwtService = jwtService;
    }


}