package com.example.demo.src.moim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostMoimReq {
    private int userIdx;
    private String moimTitle;
    private String moimDescription;
    private String startTime;
    private String endTime;
    private List<String> dates;
}
