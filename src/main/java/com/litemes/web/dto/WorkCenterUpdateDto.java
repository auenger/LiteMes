package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a work center.
 * Work center code and factory cannot be modified.
 */
public class WorkCenterUpdateDto {

    @Size(max = 50, message = "工作中心名称长度不能超过50个字符")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
