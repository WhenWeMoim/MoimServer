package com.example.demo.src.moim.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MoimInfo {
    private int moimIdx;
    private String moimTitle;
    private String moimDescription;
    private int masterUserIdx;
    private String startTime;
    private String endTime;
}
