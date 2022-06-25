package com.example.demo.src.moim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchMoimUserScheduleReq {
    private int moimIdx;
    private int userIdx;
    private String schedule;
}
