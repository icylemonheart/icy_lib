package com.icylib.upgrade;

public class UpgradeType {
    public final int attributeIndex;
    public final String attributeType;

    public UpgradeType(int attributeIndex, String attributeType) {
        this.attributeIndex = attributeIndex;
        this.attributeType = attributeType;
    }

    public static UpgradeType attribute(int index, String type) {
        return new UpgradeType(index, type);
    }
}
