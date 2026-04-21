package com.litemes.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating a process.
 * Process code and work center cannot be modified.
 */
public class ProcessUpdateDto {

    @Size(max = 50, message = "工序名称长度不能超过50个字符")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
