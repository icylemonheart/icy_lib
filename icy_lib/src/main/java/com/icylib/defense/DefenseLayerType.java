package com.icylib.defense;

/**
 * 防御层级类型枚举。
 *
 * priority 数值越小越优先承受伤害：
 * - SHIELD（护盾）最先被消耗
 * - ARMOR（护甲）在护盾耗尽后承受剩余伤害
 *
 * 未来可扩展更多层级（如：WARD 结界、BARRIER 屏障），
 * 只需添加新的枚举常量并指定 priority 即可。
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
