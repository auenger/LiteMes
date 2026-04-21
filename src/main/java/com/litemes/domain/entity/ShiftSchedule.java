package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 班制实体.
 * Represents a shift schedule pattern (e.g., "三班两运转", "正常班").
 * Shift code is immutable after creation.
 */
@TableName("shift_schedule")
public class ShiftSchedule extends SoftDeleteEntity {

    /**
     * 班制编码，创建后不可修改
     */
    private String shiftCode;

    /**
     * 班制名称
     */
    private String name;

    /**
     * 是否默认：1=是，0=否
     * 同一时刻只能有一个默认班制
     */
    private Integer isDefault;

    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status;

    public ShiftSchedule() {
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

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
