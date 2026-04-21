package com.litemes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a material version.
 */
public class MaterialVersionCreateDto {

    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    @NotBlank(message = "版本号不能为空")
    @Size(max = 20, message = "版本号长度不能超过20个字符")
    private String versionNo;

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
}
