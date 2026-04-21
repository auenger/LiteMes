package com.litemes.domain.enums;

/**
 * Basic category enumeration for materials.
 * Represents the fundamental classification of materials in PCB manufacturing.
 */
public enum BasicCategory {

    FINISHED_PRODUCT("成品"),
    SEMI_FINISHED("半成品"),
    RAW_MATERIAL("原材料"),
    SPARE_PART("备件");

    private final String label;

    BasicCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Check if the given value is a valid basic category.
     */
    public static boolean isValid(String value) {
        if (value == null) return false;
        for (BasicCategory cat : values()) {
            if (cat.name().equals(value) || cat.label.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
