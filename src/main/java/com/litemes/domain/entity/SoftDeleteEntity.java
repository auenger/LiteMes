package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * Base entity with soft delete support.
 * Entities extending this class will use logical deletion instead of physical deletion.
 * The 'deleted' field is automatically handled by MyBatis-Plus @TableLogic.
 */
public abstract class SoftDeleteEntity extends BaseEntity {

    @TableLogic
    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
