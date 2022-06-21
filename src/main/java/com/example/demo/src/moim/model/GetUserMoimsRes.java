package com.example.demo.src.moim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class GetUserMoimsRes {
    private List<MoimBriefInfo> moimInfo;
}
