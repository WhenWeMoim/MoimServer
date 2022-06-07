package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.POST_USERS_EMPTY_EMAIL;
import static com.example.demo.config.BaseResponseStatus.POST_USERS_INVALID_EMAIL;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * test1 : pathvariable 이용 방법
     * dev 서버 연결 테스트
     * pathvariable을 이용해서 userIdx를 받고 user 정보를 전달하기
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUserByIdxPathvariable(@PathVariable("userIdx")int userIdx) {
        try{

            GetUserRes getUsersRes = userProvider.getUsersByIdx(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * test2 : querystring 이용 방법
     * querystring을 이용해서 user Idx를 받고 user 정보를 전달하기
     * [GET] localhost:9000/users/aaa?UserIdx=1
     */
    @ResponseBody
    @GetMapping("/aaa")
    public BaseResponse<GetUserRes> getUserByIdxQuerystring(@RequestParam(required = true) int UserIdx) {
        try{
            GetUserRes getUserRes = userProvider.getUsersByIdx(UserIdx);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /** 수정예정: title과 moimIdx 모두 반환하도록 수정할 예정임
     * Moim Title 조회 API
     * querystring을 통해서 userIdx를 얻어서 유저가 속한 Moim의 title의 string list를 전달한다.
     * [GET] localhost:9000/users/moimtitles?UserIdx=1
     */
    
    @ResponseBody
    @GetMapping("/moimtitles")
    public BaseResponse<GetUserMoimTitles> getUserMoimTitles(@RequestParam(required = true) int UserIdx) {
        try{
            GetUserMoimTitles getUserMoimTitles = userProvider.getUserMoimTitlesByUserIdx(UserIdx);
            return new BaseResponse<>(getUserMoimTitles);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Moim 추가 API
     * json body를 전달받아서 Moim을 추가한다.
     * dates의 element 수만큼 MoimDate 테이블 데이터가 생긴다.
     * MoimDate마다 masterUser의 personalSchedule 테이블 데이터가 생긴다.
     * [POST] localhost:9000/users/moim
     */
    /**
     * JSON RAW Body의 형태는 다음과 같다.
     {
        "userIdx" : 3,
        "moimTitle" : "dummyTitle5",
        "moimDescription" : "This is dummyTitle5",
        "startTime" : "07",
        "endTime" : "14",
        "dates" : [
            "2022-05-03",
            "2022-05-04"
        ]
     }
     */
    @ResponseBody
    @PostMapping("/moim")
    public BaseResponse<PostMoimRes> createUser(@RequestBody PostMoimReq postMoimReq) {
        try{
            PostMoimRes postMoimRes = userService.createMoim(postMoimReq);
            return new BaseResponse<>(postMoimRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * Moim 조회 API
     * moimIdx를 받아서 moim의 모든 정보를 유저에게 전달한다.
     * [Get] localhost:9000/users/moimschedule?moimIdx=1
     */
    @ResponseBody
    @GetMapping("/moimschedule")
    public BaseResponse<Moim> getMoim(@RequestParam(required = true) int MoimIdx) {
        try {
            Moim moim = userProvider.getMoim(MoimIdx);
            return new BaseResponse<>(moim);
        } catch(BaseException exception){
        return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * Personal schedule 추가 API
     * moimIdx와 개인 schedule 정보를 받아서 수정한다.
     * [PATCH]
     */

    /**
     * Moim 삭제 API
     */

    /**
     * MoimUser 추가 API
     * MoimDate 테이블 데이터 마다 추가된 유저의 personalSchedule 테이블 데이터가 생긴다.
     */
    /*
    @ResponseBody
    @PostMapping("/moimuser")
    public BaseResponse<PostMoimRes> addUser(@RequestBody PostMoimReq postMoimReq) {
        try{
            PostMoimRes postMoimRes = userService.createMoim(postMoimReq);
            return new BaseResponse<>(postMoimRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    */

}
