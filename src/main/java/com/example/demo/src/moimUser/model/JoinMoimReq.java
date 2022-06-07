package com.example.demo.src.moimUser.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JoinMoimReq {
    private int moimIdx;
    private int userIdx;
    private String passwd;
}
