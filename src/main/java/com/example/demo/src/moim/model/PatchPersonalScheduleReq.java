package com.example.demo.src.moim.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchPersonalScheduleReq {
    private int userIdx;
    private UserSchedule userSchedule;
}
