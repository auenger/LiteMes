package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalTime;

/**
 * DTO for updating a shift.
 * Shift code is immutable and cannot be changed.
 */
public class ShiftUpdateDto {

    @Size(max = 50, message = "班次名称不能超过50个字符")
    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer crossDay;

    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
