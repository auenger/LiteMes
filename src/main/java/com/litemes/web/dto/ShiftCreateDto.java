package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

/**
 * DTO for creating a shift.
 */
public class ShiftCreateDto {

    @NotNull(message = "所属班制ID不能为空")
    private Long shiftScheduleId;

    @NotBlank(message = "班次编码不能为空")
    @Size(max = 50, message = "班次编码不能超过50个字符")
    private String shiftCode;

    @NotBlank(message = "班次名称不能为空")
    @Size(max = 50, message = "班次名称不能超过50个字符")
    private String name;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    private Integer crossDay;

    public Long getShiftScheduleId() {
        return shiftScheduleId;
    }

    public void setShiftScheduleId(Long shiftScheduleId) {
        this.shiftScheduleId = shiftScheduleId;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getCrossDay() {
        return crossDay;
    }

    public void setCrossDay(Integer crossDay) {
        this.crossDay = crossDay;
    }
}
