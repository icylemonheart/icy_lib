package com.icylib.defense;

import net.minecraft.world.entity.player.Player;

/**
 * 防御层接口：实现类自管数据，由 DefenseSystemManager 统一调度。
 */
public interface DefenseLayer {

    DefenseLayerType getType();

    float getCurrent(Player player);

    float getMax(Player player);

    /**
     * 返回未被该层吸收、需继续传递的伤害（≥0）。
     */
    float applyDamage(Player player, float damage);

    boolean isActive(Player player);
}
