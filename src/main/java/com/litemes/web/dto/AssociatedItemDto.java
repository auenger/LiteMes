package com.litemes.web.dto;

import java.util.List;

/**
 * DTO for saving associations (factory, work center, or process IDs) to a permission group.
 */
public class AssociatedItemDto {

    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
