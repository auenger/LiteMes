package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalTime;

/**
 * 班次实体.
 * Represents a shift period within a shift schedule (e.g., "白班 08:00-17:00").
 * Shift code is immutable after creation.
 * Supports cross-day shifts (e.g., night shift 22:00-06:00).
 */
@TableName("shift")
public class Shift extends SoftDeleteEntity {

    /**
     * 所属班制ID
     */
    private Long shiftScheduleId;

    /**
     * 班次编码，创建后不可修改
     */
    private String shiftCode;

    /**
     * 班次名称
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 是否跨天：1=是，0=否
     */
    private Integer crossDay;

    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status;

    public Shift() {
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Calculate work hours for this shift.
     * For cross-day shifts, adds 24 hours to end time before calculation.
     *
     * @return work hours as a double (e.g., 8.0 for 8 hours)
     */
    public double calculateWorkHours() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }
        long startMinutes = startTime.toSecondOfDay() / 60;
        long endMinutes = endTime.toSecondOfDay() / 60;
        if (crossDay != null && crossDay == 1 && endMinutes <= startMinutes) {
            endMinutes += 24 * 60;
        }
        return (endMinutes - startMinutes) / 60.0;
    }
}
