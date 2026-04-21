package com.litemes.domain.enums;

/**
 * Management status enumeration for equipment.
 * Represents the management lifecycle state of equipment.
 */
public enum ManageStatus {

    IN_USE("使用中"),
    IDLE("闲置"),
    SCRAPPED("报废");

    private final String label;

    ManageStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Check if the given value is a valid manage status.
     */
    public static boolean isValid(String value) {
        if (value == null) return false;
        for (ManageStatus status : values()) {
            if (status.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
