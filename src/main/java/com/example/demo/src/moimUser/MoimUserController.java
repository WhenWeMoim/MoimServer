package com.example.demo.src.moimUser;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.moim.model.PostMoimReq;
import com.example.demo.src.moim.model.PostMoimRes;
import com.example.demo.src.moim.model.TestAddUser;
import com.example.demo.src.moimUser.model.JoinMoimReq;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moimUsers")
public class MoimUserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MoimUserProvider moimUserProvider;
    @Autowired
    private final MoimUserService moimUserService;
    @Autowired
    private final JwtService jwtService;


    public MoimUserController(MoimUserProvider moimUserProvider, MoimUserService moimUserService, JwtService jwtService) {
        this.moimUserProvider = moimUserProvider;
        this.moimUserService = moimUserService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/moim")
    public BaseResponse<String> joimMoim(@RequestBody JoinMoimReq joinMoimReq) {
        try {
            String response = moimUserService.joinMoim(joinMoimReq.getMoimIdx(), joinMoimReq.getUserIdx(), joinMoimReq.getPasswd());
            return new BaseResponse<>(response);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
