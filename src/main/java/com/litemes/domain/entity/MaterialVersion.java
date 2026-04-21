package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * MaterialVersion entity.
 * Represents a version of a material (e.g., A.1, A.2).
 * Tracks material specification changes over time.
 * Combined unique constraint: material_id + version_no.
 */
@TableName("material_version")
public class MaterialVersion extends SoftDeleteEntity {

    private Long materialId;

    private String versionNo;

    private Integer status;

    public MaterialVersion() {
    }

    public MaterialVersion(Long materialId, String versionNo) {
        this.materialId = materialId;
        this.versionNo = versionNo;
        this.status = 1;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
