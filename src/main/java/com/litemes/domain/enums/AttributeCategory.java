package com.litemes.domain.enums;

/**
 * Attribute category enumeration for materials.
 * Represents how a material is sourced: purchased, self-manufactured, or both.
 */
public enum AttributeCategory {

    PURCHASED("采购件"),
    SELF_MANUFACTURED("自制件"),
    PURCHASED_AND_SELF("采购&自制件");

    private final String label;

    AttributeCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Check if the given value is a valid attribute category.
     */
    public static boolean isValid(String value) {
        if (value == null) return false;
        for (AttributeCategory cat : values()) {
            if (cat.name().equals(value) || cat.label.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
