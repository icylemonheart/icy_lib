package com.icylib.defense;

import net.minecraft.world.entity.player.Player;

/**
 * 防御层接口。
 *
 * 抽象出所有可吸收伤害的防御系统（护盾、护甲、未来扩展的结界等），
 * 由 {@link DefenseSystemManager} 统一调度。
 *
 * 实现类负责自身数据存储与状态管理，无需订阅伤害事件。
 */
public interface DefenseLayer {

    /**
     * 获取该防御层的类型标识。
     */
    DefenseLayerType getType();

    /**
     * 获取玩家当前剩余的防御值。
     */
    float getCurrent(Player player);

    /**
     * 获取玩家防御值上限。
     */
    float getMax(Player player);

    /**
     * 对该防御层应用伤害。
     *
     * @param player  目标玩家
     * @param damage  待吸收的伤害数值
     * @return        未能被该层吸收而需要继续传递的伤害（≥0）
     */
    float applyDamage(Player player, float damage);

    /**
     * 该防御层是否处于激活状态（仍有可吸收的数值）。
     */
    boolean isActive(Player player);
}
