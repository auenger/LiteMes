package com.litemes.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for directly assigning factory/work-center/process permissions to a user.
 */
public class DirectAssignDto {

    @NotNull(message = "ID列表不能为空")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
