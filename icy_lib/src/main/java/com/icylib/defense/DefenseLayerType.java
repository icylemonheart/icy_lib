package com.icylib.defense;

/**
 * 防御层类型：priority 越小越先消耗（SHIELD=0，ARMOR=1）。
 */
public enum DefenseLayerType {
    SHIELD(0),
    ARMOR(1);

    private final int priority;

    DefenseLayerType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
