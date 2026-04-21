package com.litemes.domain.enums;

/**
 * Running status enumeration for equipment.
 * Represents the current operational state of equipment.
 */
public enum RunningStatus {

    RUNNING("运行"),
    FAULT("故障"),
    SHUTDOWN("停机"),
    MAINTENANCE("维修保养");

    private final String label;

    RunningStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Check if the given value is a valid running status.
     */
    public static boolean isValid(String value) {
        if (value == null) return false;
        for (RunningStatus status : values()) {
            if (status.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
