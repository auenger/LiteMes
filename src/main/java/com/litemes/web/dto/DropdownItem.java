package com.litemes.web.dto;

/**
 * Generic dropdown item DTO for select/option lists.
 * Used for company, factory, department, shift-schedule dropdowns.
 */
public class DropdownItem {

    private Long id;
    private String code;
    private String name;
    private Long parentId;

    public DropdownItem() {
    }

    public DropdownItem(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public DropdownItem(Long id, String code, String name, Long parentId) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
